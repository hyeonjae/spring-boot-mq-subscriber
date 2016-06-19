package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class MqConfiguration {
    static final private String RABBITMQ_HOST = "127.0.0.1";
    static final private String RABBITMQ_USERNAME = "guest";
    static final private String RABBITMQ_PASSWORD = "guest";
    static final private int RABBITMQ_PORT = 5672;

    static final private String RABBITMQ_QUEUE = "sample.queue";

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(RABBITMQ_HOST);
        connectionFactory.setUsername(RABBITMQ_USERNAME);
        connectionFactory.setPassword(RABBITMQ_PASSWORD);
        connectionFactory.setPort(RABBITMQ_PORT);
        return connectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        DefaultClassMapper defaultClassMapper = new DefaultClassMapper();
        defaultClassMapper.setDefaultType(Map.class);
        messageConverter.setClassMapper(defaultClassMapper);
        return messageConverter;
    }

    @Bean
    public Queue hookQueue() {
        return new Queue(RABBITMQ_QUEUE);
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        container.setQueues(hookQueue());
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(queueListener(), jsonMessageConverter());
        container.setMessageListener(messageListenerAdapter);

        return container;
    }

    @Bean
    public MessageListener queueListener() {
        return new MessageListenerImpl();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
