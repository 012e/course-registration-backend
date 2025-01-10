package com.u012e.session_auth_db.queue.registration;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.queue.registration.dto.UpdateRegistrationDto;
import com.u012e.session_auth_db.repository.CourseRepository;
import com.u012e.session_auth_db.repository.StudentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@RabbitListener(queues = "registration")
public class RegistrationConsumer {
    final private StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @RabbitListener(queues = "registration")
    public void consume(@Payload List<@Valid UpdateRegistrationDto> dtos) {
        log.trace(" [x] Received '{}'", dtos);

        var processedStudents = new ArrayList<Student>(dtos.size());
        for (UpdateRegistrationDto dto : dtos) {
            // TODO: better error handling
            final var student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow();

            final var registeredCourses = courseRepository.findByStudents(student);

            final var courses = courseRepository.findAllById(
                    dto.getCourseIds()
            );

            final var operation = dto.getOperation();

            switch (operation) {
                case ADD_COURSES -> {
                    registeredCourses.addAll(courses);
                }
                case REMOVE_COURSES -> {
                    courses.forEach(registeredCourses::remove);
                }
            }
            student.setCourses(registeredCourses);
            processedStudents.add(student);
        }

        studentRepository.saveAll(processedStudents);
    }
}
