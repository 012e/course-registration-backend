package com.u012e.session_auth_db.queue.counter;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CounterQueue {
    @Bean("counter")
    public Queue queue() {
        return new Queue("counter");
    }
}
