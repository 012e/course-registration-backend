package com.u012e.session_auth_db.queue.registration;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistrationQueue {
    @Bean("registration")
    public Queue queue() {
        return new Queue("registration");
    }
}
