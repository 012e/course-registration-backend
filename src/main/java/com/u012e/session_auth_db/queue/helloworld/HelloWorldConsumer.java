package com.u012e.session_auth_db.queue.helloworld;

import com.u012e.session_auth_db.queue.helloworld.dto.MessageDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RabbitListener(queues = "hello world")
public class HelloWorldConsumer {
    @RabbitHandler
    public void receive(@Valid @Payload List<MessageDto> message) {
        log.info(" [x] Received '{}'", message.getFirst()
                .getMessage());
    }
}
