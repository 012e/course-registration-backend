package com.u012e.session_auth_db.queue.helloworld;

import com.u012e.session_auth_db.queue.helloworld.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Lazy(false)
@Component
@Slf4j
public class HelloWorldProducer {
    private final RabbitTemplate template;
    private final Queue queue;
    private final MessageConverter messageConverter;

    public HelloWorldProducer(RabbitTemplate template, @Qualifier("hello world") Queue queue, MessageConverter messageConverter) {
        this.template = template;
        this.queue = queue;
        this.messageConverter = messageConverter;
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void send() {
        var message = MessageDto.builder()
                .message("Hello world")
                .build();
        template.convertAndSend(queue.getName(), message);
//        log.info(" [x] Sent '{}'", message);
    }
}
