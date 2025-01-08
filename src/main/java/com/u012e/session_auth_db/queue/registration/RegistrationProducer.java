package com.u012e.session_auth_db.queue.registration;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.queue.registration.dto.RegistrationOperation;
import com.u012e.session_auth_db.queue.registration.dto.UpdateRegistrationDto;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegistrationProducer {
    private final Queue queue;
    private final RabbitTemplate rabbitTemplate;

    public RegistrationProducer(@Qualifier("registration") Queue queue, RabbitTemplate rabbitTemplate) {
        this.queue = queue;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void addCourses(Set<Course> courses, Student student) {
        var dto = UpdateRegistrationDto.builder()
                .courseIds(courses.stream()
                        .map(Course::getId)
                        .collect(Collectors.toSet()))
                .studentId(student.getId())
                .operation(RegistrationOperation.ADD_COURSES)
                .build();
        rabbitTemplate.convertAndSend(queue.getName(), dto);
    }

    public void removeCourses(Set<Course> courses, Student student) {
        var dto = UpdateRegistrationDto.builder()
                .courseIds(courses.stream()
                        .map(Course::getId)
                        .collect(Collectors.toSet()))
                .studentId(student.getId())
                .operation(RegistrationOperation.REMOVE_COURSES)
                .build();
        rabbitTemplate.convertAndSend(queue.getName(), dto);
    }
}
