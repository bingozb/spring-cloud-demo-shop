package com.bhnote.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bhnote.config.RabbitMqConfig;
import com.bhnote.constant.CacheKeyConstant;
import com.bhnote.enums.BizCodeEnum;
import com.bhnote.enums.OrderStateEnum;
import com.bhnote.exception.BizException;
import com.bhnote.feign.ProductFeignService;
import com.bhnote.feign.UserFeignService;
import com.bhnote.interceptor.LoginInterceptor;
import com.bhnote.mapper.OrderItemMapper;
import com.bhnote.mapper.OrderMapper;
import com.bhnote.model.OrderDO;
import com.bhnote.model.OrderItemDO;
import com.bhnote.model.PageResult;
import com.bhnote.model.dto.OrderCloseMessage;
import com.bhnote.model.ro.ConfirmOrderRequest;
import com.bhnote.model.ro.LockProductRequest;
import com.bhnote.model.ro.OrderItemRequest;
import com.bhnote.model.vo.OrderAddressVO;
import com.bhnote.model.vo.OrderItemVO;
import com.bhnote.model.vo.OrderVO;
import com.bhnote.model.vo.ProductVO;
import com.bhnote.service.OrderService;
import com.bhnote.utils.CodeUtil;
import com.bhnote.utils.JsonData;
import com.bhnote.utils.SnowFlakeUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * ????????? ???????????????
 * </p>
 *
 * @author Bingo
 * @since 2022-01-11
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserFeignService userFeignService;
    @Resource
    private ProductFeignService productFeignService;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RabbitMqConfig rabbitMqConfig;

    @Override
    public JsonData getToken() {
        String cacheKey = String.format(CacheKeyConstant.ORDER_CONFIRM_TOKEN_KEY, LoginInterceptor.threadLocal.get().getId());
        String orderToken = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(cacheKey, orderToken, 30, TimeUnit.MINUTES);
        return JsonData.buildSuccess(orderToken);
    }

    /**
     * ??????????????????????????????????????????????????? @GlobalTransactional ?????? @Transactional
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonData confirmOrder(ConfirmOrderRequest orderRequest) {
        String orderOutTradeNo = CodeUtil.randomNumberAndLetter(32);
        // ????????????
        this.checkToken(orderRequest.getToken());
        // ????????????????????????
        OrderAddressVO addressVO = this.getUserAddress(orderRequest.getAddressId());
        // ????????????????????????
        List<ProductVO> productVoList = this.getUserOrderItem(orderRequest.getProductList());
        // ??????????????????
        this.lockProductStock(orderRequest.getProductList(), orderOutTradeNo);
        // ????????????
        OrderDO orderDO = this.createOrder(orderOutTradeNo, LoginInterceptor.threadLocal.get().getId(), orderRequest.getProductList(), productVoList, addressVO);
        // ???????????????
        this.createOrderItem(orderDO, orderRequest.getProductList(), productVoList);
        // ??????????????????
        this.sendCloseOrderMessage(orderOutTradeNo);

        return JsonData.buildSuccess();
    }

    @Override
    public JsonData pageUserOrderList(Integer page, Integer size, String state) {
        LambdaQueryWrapper<OrderDO> queryWrapper = new LambdaQueryWrapper<OrderDO>().eq(OrderDO::getUserId, LoginInterceptor.threadLocal.get().getId());
        if (StringUtils.isNotBlank(state)) {
            queryWrapper = queryWrapper.eq(OrderDO::getState, state);
        }
        IPage<OrderDO> iPage = orderMapper.selectPage(new Page<>(page, size), queryWrapper);
        List<OrderVO> orderVOList = iPage.getRecords().stream().map(orderDO -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orderDO, orderVO);
            orderVO.setReceiverAddress(JSON.parseObject(orderDO.getReceiverAddress(), OrderAddressVO.class));
            orderVO.setOrderItems(orderItemMapper.selectList(new LambdaQueryWrapper<OrderItemDO>().eq(OrderItemDO::getOrderId, orderDO.getId())).stream().map(orderItemDO -> {
                OrderItemVO orderItemVO = new OrderItemVO();
                BeanUtils.copyProperties(orderItemDO, orderItemVO);
                return orderItemVO;
            }).collect(Collectors.toList()));
            return orderVO;
        }).collect(Collectors.toList());

        PageResult pageResult = PageResult.builder().total(iPage.getTotal()).pages(iPage.getPages()).list(orderVOList).build();
        return JsonData.buildSuccess(pageResult);
    }

    @Override
    public JsonData queryOrderState(String outTradeNo) {
        OrderDO orderDO = orderMapper.selectOne(new LambdaQueryWrapper<OrderDO>().eq(OrderDO::getOutTradeNo, outTradeNo));
        if (orderDO == null) {
            return JsonData.buildError(BizCodeEnum.ORDER_NOT_EXIST);
        }
        return JsonData.buildSuccess(orderDO.getState());
    }

    @Override
    public boolean closeOrder(String outTradeNo) {
        OrderDO orderDO = orderMapper.selectOne(new LambdaQueryWrapper<OrderDO>().eq(OrderDO::getOutTradeNo, outTradeNo));
        if (orderDO == null) {
            log.warn("??????????????????????????????????????????????????????????????????:outTradeNo={}", outTradeNo);
            return true;
        }
        if (orderDO.getState().equals(OrderStateEnum.PAY.name())) {
            log.info("???????????????????????????????????????????????????????????????outTradeNo={}", outTradeNo);
            return true;
        }
        // ????????????
        orderMapper.update(null, new LambdaUpdateWrapper<OrderDO>()
                .set(OrderDO::getState, OrderStateEnum.CANCEL.name())
                .eq(OrderDO::getOutTradeNo, outTradeNo)
                .eq(OrderDO::getState, OrderStateEnum.NEW.name()));
        log.info("???????????????????????????????????????????????????????????????CANCEL???outTradeNo={}", outTradeNo);
        return true;
    }

    /**
     * ??????token?????????????????????????????????
     */
    private void checkToken(String token) {
        if (StringUtils.isBlank(token)) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_TOKEN_NOT_EXIST);
        }
        // lua???????????????????????? 1.??????key??????token????????????????????? 2.??????????????? ???????????????????????????
        String cacheKey = String.format(CacheKeyConstant.ORDER_CONFIRM_TOKEN_KEY, LoginInterceptor.threadLocal.get().getId());
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), List.of(cacheKey), token);
        if (result == null || result == 0L) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_TOKEN_EQUAL_FAIL);
        }
    }

    /**
     * ????????????????????????
     */
    private OrderAddressVO getUserAddress(long addressId) {
        JsonData addressData = userFeignService.findById(addressId);
        if (addressData.getCode() != 0) {
            log.error("????????????????????????:{}", addressData);
            throw new BizException(BizCodeEnum.ADDRESS_NO_EXITS);
        }
        return addressData.getData(new TypeReference<>() {
        });
    }

    /**
     * ????????????????????????
     */
    private List<ProductVO> getUserOrderItem(List<OrderItemRequest> orderItemRequests) {
        List<Long> productIds = orderItemRequests.stream().map(OrderItemRequest::getProductId).collect(Collectors.toList());
        List<ProductVO> productVoList = productFeignService.getBatchProduct(productIds);
        if (productVoList == null || productVoList.size() == 0) {
            log.error("????????????????????????");
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_ITEM_NOT_EXIST);
        }
        return productVoList;
    }

    /**
     * ??????????????????
     */
    private void lockProductStock(List<OrderItemRequest> orderItemRequests, String orderOutTradeNo) {
        LockProductRequest lockProductRequest = new LockProductRequest();
        lockProductRequest.setOrderOutTradeNo(orderOutTradeNo);
        lockProductRequest.setOrderItemList(orderItemRequests);
        JsonData jsonData = productFeignService.lockProductStock(lockProductRequest);
        if (jsonData.getCode() != 0) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_LOCK_PRODUCT_FAIL);
        }
    }

    /**
     * ????????????
     */
    private OrderDO createOrder(String orderOutTradeNo, Long userId, List<OrderItemRequest> productList, List<ProductVO> productVoList, OrderAddressVO addressVO) {
        OrderDO orderDO = new OrderDO();
        orderDO.setId(SnowFlakeUtil.getInstance().nextId());
        orderDO.setOutTradeNo(orderOutTradeNo);
        orderDO.setUserId(userId);
        // ????????????
        BigDecimal price = new BigDecimal("0.00");
        Map<Long, List<ProductVO>> productMap = productVoList.stream().collect(Collectors.groupingBy(ProductVO::getId));
        for (OrderItemRequest orderItemRequest : productList) {
            ProductVO productVO = productMap.get(orderItemRequest.getProductId()).get(0);
            price = price.add((productVO.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.getBuyNum()))));
        }
        orderDO.setTotalPrice(price);
        orderDO.setState(OrderStateEnum.NEW.name());
        orderDO.setReceiverAddress(JSON.toJSONString(addressVO));
        orderMapper.insert(orderDO);
        return orderDO;
    }

    /**
     * ???????????????
     */
    private void createOrderItem(OrderDO orderDO, List<OrderItemRequest> productList, List<ProductVO> productVoList) {
        Map<Long, List<ProductVO>> productMap = productVoList.stream().collect(Collectors.groupingBy(ProductVO::getId));
        List<OrderItemDO> orderItems = productList.stream().map(obj -> {
            OrderItemDO item = new OrderItemDO();
            item.setId(SnowFlakeUtil.getInstance().nextId());
            item.setOrderId(orderDO.getId());
            item.setOutTradeNo(orderDO.getOutTradeNo());
            item.setProductId(obj.getProductId());
            ProductVO productVO = productMap.get(obj.getProductId()).get(0);
            item.setPrice(productVO.getPrice());
            item.setBuyNum(obj.getBuyNum());
            item.setTotalPrice(productVO.getPrice().multiply(BigDecimal.valueOf(obj.getBuyNum())));
            return item;
        }).collect(Collectors.toList());
        orderItemMapper.insertBatchSomeColumn(orderItems);
    }

    /**
     * ??????????????????????????????
     */
    private void sendCloseOrderMessage(String orderOutTradeNo) {
        OrderCloseMessage orderCloseMessage = new OrderCloseMessage();
        orderCloseMessage.setOutTradeNo(orderOutTradeNo);
        rabbitTemplate.convertAndSend(rabbitMqConfig.getOrderEventExchange(), rabbitMqConfig.getOrderCloseDelayRoutingKey(), JSON.toJSONString(orderCloseMessage));
    }
}
