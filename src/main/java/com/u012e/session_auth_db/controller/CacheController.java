package com.u012e.session_auth_db.controller;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.repository.CourseRepository;
import com.u012e.session_auth_db.repository.StudentRepository;
import com.u012e.session_auth_db.service.registration.DependencyChecker;
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

    @GetMapping("prepareCourses")
    public String prepareCourseCaching() {
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
        return "course have just been cached";
    }
}
