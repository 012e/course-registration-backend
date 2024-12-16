package com.u012e.session_auth_db.controller;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.repository.CourseRepository;
import com.u012e.session_auth_db.repository.StudentRepository;
import com.u012e.session_auth_db.service.registration.DependencyChecker;
import com.u012e.session_auth_db.service.syncer.ParticipantCounterSyncer;
import com.u012e.session_auth_db.utils.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class CacheController {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final DependencyChecker dependencyChecker;
    private final ParticipantCounterSyncer participantCounterSyncer;

    @GetMapping("sync/counter")
    public GenericResponse<Object> syncCounter() {
        participantCounterSyncer.sync();
        return GenericResponse.builder()
                .message("synced counter successfully")
                .data(null)
                .build();
    }

    @GetMapping("prepareCourses")
    public GenericResponse<Object> prepareCourseCaching() {
        var students = studentRepository.findAll();
        var top10Courses = courseRepository.findTop10ByOrderByIdAsc();
        for (var student : students) {
            var learnedSubjects = student.getCourses()
                    .stream()
                    .map(Course::getSubject)
                    .collect(Collectors.toSet());
            for (var course : top10Courses) {
                dependencyChecker.checkDependency(course, learnedSubjects);
            }

        }
        return GenericResponse.builder()
                .message("prepared course successfully")
                .data(null)
                .build();
    }
}
