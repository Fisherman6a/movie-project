package com.movie_back.backend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 * 定义队列、交换机、绑定关系
 */
@Configuration
public class RabbitMQConfig {

    // ========== 队列名称常量 ==========
    public static final String RATING_UPDATE_QUEUE = "rating.update";
    public static final String LIKE_NOTIFICATION_QUEUE = "like.notification";
    public static final String INDEX_UPDATE_QUEUE = "index.update";
    public static final String LOG_COLLECT_QUEUE = "log.collect";

    // ========== 交换机名称常量 ==========
    public static final String MOVIE_EXCHANGE = "movie.exchange";

    // ========== 路由键常量 ==========
    public static final String RATING_UPDATE_ROUTING_KEY = "rating.update";
    public static final String LIKE_NOTIFICATION_ROUTING_KEY = "like.notification";
    public static final String INDEX_UPDATE_ROUTING_KEY = "index.update";
    public static final String LOG_COLLECT_ROUTING_KEY = "log.collect";

    /**
     * JSON 消息转换器
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate 配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    /**
     * 监听器容器工厂配置
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    // ========== 定义交换机 ==========
    @Bean
    public DirectExchange movieExchange() {
        return new DirectExchange(MOVIE_EXCHANGE, true, false);
    }

    // ========== 定义队列 ==========
    @Bean
    public Queue ratingUpdateQueue() {
        return QueueBuilder.durable(RATING_UPDATE_QUEUE).build();
    }

    @Bean
    public Queue likeNotificationQueue() {
        return QueueBuilder.durable(LIKE_NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Queue indexUpdateQueue() {
        return QueueBuilder.durable(INDEX_UPDATE_QUEUE).build();
    }

    @Bean
    public Queue logCollectQueue() {
        return QueueBuilder.durable(LOG_COLLECT_QUEUE).build();
    }

    // ========== 绑定队列到交换机 ==========
    @Bean
    public Binding ratingUpdateBinding() {
        return BindingBuilder
                .bind(ratingUpdateQueue())
                .to(movieExchange())
                .with(RATING_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Binding likeNotificationBinding() {
        return BindingBuilder
                .bind(likeNotificationQueue())
                .to(movieExchange())
                .with(LIKE_NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public Binding indexUpdateBinding() {
        return BindingBuilder
                .bind(indexUpdateQueue())
                .to(movieExchange())
                .with(INDEX_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Binding logCollectBinding() {
        return BindingBuilder
                .bind(logCollectQueue())
                .to(movieExchange())
                .with(LOG_COLLECT_ROUTING_KEY);
    }
}
