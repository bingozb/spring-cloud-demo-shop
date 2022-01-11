package com.bhnote.config;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bingo
 * @date 2022/1/11
 */
@Configuration
@Data
public class RabbitMqConfig {

    @Value("${rabbitmq-config.ttl}")
    private Integer ttl;

    @Value("${rabbitmq-config.order-event-exchange}")
    private String orderEventExchange;

    @Value("${rabbitmq-config.order-close-delay-queue}")
    private String orderCloseDelayQueue;

    @Value("${rabbitmq-config.order-close-delay-routing-key}")
    private String orderCloseDelayRoutingKey;

    @Value("${rabbitmq-config.order-close-queue}")
    private String orderCloseQueue;

    @Value("${rabbitmq-config.order-close-routing-key}")
    private String orderCloseRoutingKey;

    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange(orderEventExchange, true, false);
    }

    @Bean
    public Queue orderCloseDelayQueue() {
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-message-ttl", ttl);
        args.put("x-dead-letter-exchange", orderEventExchange);
        args.put("x-dead-letter-routing-key", orderCloseRoutingKey);
        return new Queue(orderCloseDelayQueue, true, false, false, args);
    }

    @Bean
    public Binding orderCloseDelayQueueBinding() {
        return new Binding(orderCloseDelayQueue, Binding.DestinationType.QUEUE, orderEventExchange, orderCloseDelayRoutingKey, null);
    }

    @Bean
    public Queue orderCloseQueue() {
        return new Queue(orderCloseQueue, true, false, false);
    }

    @Bean
    public Binding orderCloseQueueBinding() {
        return new Binding(orderCloseQueue, Binding.DestinationType.QUEUE, orderEventExchange, orderCloseRoutingKey, null);
    }
}