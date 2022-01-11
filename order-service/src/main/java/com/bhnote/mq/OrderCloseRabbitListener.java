package com.bhnote.mq;

import com.alibaba.fastjson.JSON;
import com.bhnote.model.dto.OrderCloseMessage;
import com.bhnote.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author bingo
 * @date 2022/1/11
 */
@Slf4j
@Component
@RabbitListener(queues = "${rabbitmq-config.order-close-queue}")
public class OrderCloseRabbitListener {

    @Resource
    private OrderService orderService;

    @RabbitHandler
    public void orderCloseHandler(String msg, Message message, Channel channel) throws IOException {
        log.info("msg={},message={},channel={}", msg, message, channel);
        OrderCloseMessage orderCloseMessage = JSON.parseObject(msg, OrderCloseMessage.class);
        boolean flag = orderService.closeOrder(orderCloseMessage.getOutTradeNo());
        if (flag) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } else {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
