package com.bhnote.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bhnote.config.RabbitMqConfig;
import com.bhnote.enums.BizCodeEnum;
import com.bhnote.enums.OrderStateEnum;
import com.bhnote.enums.ProductLockStateEnum;
import com.bhnote.exception.BizException;
import com.bhnote.feign.OrderFeignService;
import com.bhnote.mapper.ProductLockMapper;
import com.bhnote.mapper.ProductMapper;
import com.bhnote.model.PageResult;
import com.bhnote.model.ProductDO;
import com.bhnote.model.ProductLockDO;
import com.bhnote.model.dto.OrderLockMessage;
import com.bhnote.model.ro.LockProductRequest;
import com.bhnote.model.ro.OrderItemRequest;
import com.bhnote.model.ro.SaveProductRequest;
import com.bhnote.model.vo.ProductVO;
import com.bhnote.service.ProductService;
import com.bhnote.utils.JsonData;
import com.bhnote.utils.SnowFlakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author Bingo
 * @since 2022-01-07
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private ProductLockMapper productLockMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RabbitMqConfig rabbitMqConfig;

    @Resource
    private OrderFeignService orderFeignService;

    @Override
    public JsonData saveProduct(SaveProductRequest saveProductRequest) {
        ProductDO productDO = new ProductDO();
        BeanUtils.copyProperties(saveProductRequest, productDO);
        if (productDO.getId() == null) {
            productDO.setId(SnowFlakeUtil.getInstance().nextId());
            productDO.setLockStock(0);
            productMapper.insert(productDO);
        } else {
            productMapper.updateById(productDO);
        }
        return JsonData.buildSuccess(beanProcess(productDO));
    }

    @Override
    public JsonData getPageProduct(long page, long size) {
        IPage<ProductDO> iPage = productMapper.selectPage(new Page<>(page, size), null);
        PageResult pageResult = PageResult.builder()
                .total(iPage.getTotal())
                .pages(iPage.getPages())
                .list(iPage.getRecords().stream().map(this::beanProcess).collect(Collectors.toList()))
                .build();
        return JsonData.buildSuccess(pageResult);
    }

    @Override
    public JsonData getProductDetail(long productId) {
        ProductDO productDO = productMapper.selectById(productId);
        return JsonData.buildSuccess(beanProcess(productDO));
    }

    @Override
    public JsonData lockProductStock(LockProductRequest lockProductRequest) {
        List<OrderItemRequest> orderItemRequests = lockProductRequest.getOrderItemList();
        String orderOutTradeNo = lockProductRequest.getOrderOutTradeNo();
        for (OrderItemRequest item : orderItemRequests) {
            long productId = item.getProductId();
            int buyNum = item.getBuyNum();
            // 锁定库存
            int updateRows = productMapper.lockProductStock(productId, buyNum);
            if (updateRows == 1) {
                // 插入锁定表
                ProductLockDO productLockDO = new ProductLockDO();
                productLockDO.setId(SnowFlakeUtil.getInstance().nextId());
                productLockDO.setProductId(productId);
                productLockDO.setBuyNum(buyNum);
                productLockDO.setOutTradeNo(orderOutTradeNo);
                productLockDO.setLockState(ProductLockStateEnum.LOCK.name());
                int insertRows = productLockMapper.insert(productLockDO);
                if (insertRows == 1) {
                    // 发送延迟消息
                    OrderLockMessage orderLockTaskMessage = new OrderLockMessage();
                    orderLockTaskMessage.setLockId(productLockDO.getId());
                    orderLockTaskMessage.setOutTradeNo(orderOutTradeNo);
                    rabbitTemplate.convertAndSend(rabbitMqConfig.getEventExchange(), rabbitMqConfig.getStockReleaseDelayRoutingKey(), JSON.toJSONString(orderLockTaskMessage));
                    log.info("商品库存锁定成功:orderLockTaskMessage={}", orderLockTaskMessage);
                }
            } else {
                log.warn("商品库存不足锁定失败:orderOutTradeNo={}", orderOutTradeNo);
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_LOCK_PRODUCT_FAIL);
            }
        }
        return JsonData.buildSuccess();
    }

    @Override
    public List<ProductVO> getBatchProduct(List<Long> productIds) {
        List<ProductDO> productDoList = productMapper.selectBatchIds(productIds);
        return productDoList.stream().map(this::beanProcess).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public boolean releaseProductStock(OrderLockMessage orderLockTaskMessage) {
        // 检查锁定是否存在
        Long lockId = orderLockTaskMessage.getLockId();
        ProductLockDO productLockDO = productLockMapper.selectById(lockId);
        if (productLockDO == null) {
            log.warn("商品库存锁定不存在，消息={}", orderLockTaskMessage);
            return true;
        }
        // 锁定任务是LOCK状态才处理
        if (!productLockDO.getLockState().equals(ProductLockStateEnum.LOCK.name())) {
            log.warn("商品库存锁定状态异常：状态={}，消息={}", productLockDO.getLockState(), orderLockTaskMessage);
            return true;
        }
        // 检查订单的状态
        JsonData jsonData = orderFeignService.queryOrderState(orderLockTaskMessage.getOutTradeNo());
        if (jsonData.getCode() == 0) {
            // 正常响应，判断订单状态
            String orderState = jsonData.getData().toString();
            if (orderState.equals(OrderStateEnum.NEW.name())) {
                // 订单未支付
                log.warn("释放商品库存失败：订单未支付，orderLockTaskMessage={}", orderLockTaskMessage);
                return false;
            }
            if (orderState.equals(OrderStateEnum.PAY.name())) {
                // 订单已支付
                productLockDO.setLockState(ProductLockStateEnum.FINISH.name());
                productLockMapper.updateById(productLockDO);
                log.info("无需释放商品库存：订单已支付：orderLockTaskMessage={}", orderLockTaskMessage);
                return true;
            }
        }
        // 订单已取消，或者已关闭，修改锁定状态为取消
        productLockDO.setLockState(ProductLockStateEnum.CANCEL.name());
        productLockMapper.updateById(productLockDO);
        // 恢复商品库存
        productMapper.releaseProductStock(productLockDO.getProductId(), productLockDO.getBuyNum());
        log.info("释放商品库存成功：orderLockTaskMessage={}", orderLockTaskMessage);
        return true;
    }

    /**
     * productDO -> productVO，处理库存字段为可售库存
     */
    private ProductVO beanProcess(ProductDO productDO) {
        if (productDO == null) {
            return null;
        }
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(productDO, productVO);
        productVO.setStock(productDO.getStock() - productDO.getLockStock());
        return productVO;
    }
}
