package com.u012e.session_auth_db.queue.registration;

import com.u012e.session_auth_db.queue.registration.dto.UpdateRegistrationDto;
import com.u012e.session_auth_db.repository.StudentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@RabbitListener(queues = "registration")
public class RegistrationConsumer {
    private StudentRepository studentRepository;

    @RabbitListener(queues = "registration")
    public void consume(@Payload List<@Valid UpdateRegistrationDto> dtos) {
        log.trace(" [x] Received '{}'", dtos);

        for (UpdateRegistrationDto dto : dtos) {
            final var student = dto.getStudent();

            final var operation = dto.getOperation();

            switch (operation) {
                case ADD_COURSES -> student.getCourses().addAll(dto.getCourses());
                case REMOVE_COURSES -> student.getCourses().removeAll(dto.getCourses());
            }
        }

        studentRepository.saveAll(dtos.stream().map(UpdateRegistrationDto::getStudent).collect(Collectors.toSet()));
    }
}
