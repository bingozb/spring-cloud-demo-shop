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
 * RabbitMQ 配置
 *
 * @author bingo
 * @date 2022/1/7
 */
@Configuration
@Data
public class RabbitMqConfig {

    /**
     * 过期时间
     */
    @Value("${rabbitmq-config.ttl}")
    private Integer ttl;

    /**
     * 交换机
     */
    @Value("${rabbitmq-config.stock-event-exchange}")
    private String eventExchange;

    /**
     * 延迟队列
     */
    @Value("${rabbitmq-config.stock-release-delay-queue}")
    private String stockReleaseDelayQueue;

    /**
     * 延迟队列路由key
     */
    @Value("${rabbitmq-config.stock-release-delay-routing-key}")
    private String stockReleaseDelayRoutingKey;

    /**
     * 死信队列（消费队列）
     */
    @Value("${rabbitmq-config.stock-release-queue}")
    private String stockReleaseQueue;

    /**
     * 死信队列路由key
     */
    @Value("${rabbitmq-config.stock-release-routing-key}")
    private String stockReleaseRoutingKey;

    @Bean
    public Exchange eventExchange() {
        return new TopicExchange(eventExchange, true, false);
    }

    @Bean
    public Queue stockReleaseDelayQueue() {
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-message-ttl", ttl);
        args.put("x-dead-letter-exchange", eventExchange);
        args.put("x-dead-letter-routing-key", stockReleaseRoutingKey);
        return new Queue(stockReleaseDelayQueue, true, false, false, args);
    }

    @Bean
    public Binding stockReleaseDelayQueueBinding() {
        return new Binding(stockReleaseDelayQueue, Binding.DestinationType.QUEUE, eventExchange, stockReleaseDelayRoutingKey, null);
    }

    @Bean
    public Queue stockReleaseQueue() {
        return new Queue(stockReleaseQueue, true, false, false);
    }

    @Bean
    public Binding stockReleaseQueueBinding() {
        return new Binding(stockReleaseQueue, Binding.DestinationType.QUEUE, eventExchange, stockReleaseRoutingKey, null);
    }
}