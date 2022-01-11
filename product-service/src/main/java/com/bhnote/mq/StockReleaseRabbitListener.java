package com.bhnote.mq;

import com.alibaba.fastjson.JSON;
import com.bhnote.model.dto.OrderLockMessage;
import com.bhnote.service.ProductService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 监听下单锁定商品库存消息
 *
 * @author bingo
 * @date 2022/1/11
 */
@Slf4j
@Component
@RabbitListener(queues = "${rabbitmq-config.stock-release-queue}")
public class StockReleaseRabbitListener {

    @Resource
    private ProductService productService;

    @RabbitHandler
    public void stockReleaseHandler(String msg, Message message, Channel channel) throws IOException {
        log.info("msg={},message={},channel={}", msg, message, channel);
        OrderLockMessage orderLockTaskMessage = JSON.parseObject(msg, OrderLockMessage.class);
        boolean flag = productService.releaseProductStock(orderLockTaskMessage);
        if (flag) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } else {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
