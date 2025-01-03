package com.u012e.session_auth_db.queue.helloworld;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloWorldQueue {
    @Bean("hello world")
    public Queue queue() {
        return new Queue("hello world");
    }
}
