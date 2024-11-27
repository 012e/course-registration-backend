package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.repository.StudentRepository;
import com.u012e.session_auth_db.service.CourseService;
import com.u012e.session_auth_db.utils.RegistrationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseCourseRegistrationService implements CourseRegistrationService {
    private final DependencyChecker dependencyChecker;
    private final CourseService courseService;
    private final StudentRepository studentRepository;

    @Override
    public RegistrationResult register(Student student, List<Long> courseIds) {
        var courses = new HashSet<>(courseService.getAllById(courseIds));
        var learnedSubjects = student
                .getResults()
                .parallelStream()
                .map(result -> result.getCourse()
                        .getSubject())
                .collect(Collectors.toSet());

        if (learnedSubjects.isEmpty()) {
            log.warn("Student {} has not learned any subjects", student);
        } else {
            for (var subject : learnedSubjects) {
                log.trace("Student {}: learned subject: {}", student, subject);
            }
        }

        log.trace("Checking dependencies for student {} and courses {}", student, courses);
        var result = dependencyChecker.checkDependencies(courses, learnedSubjects);
        // TODO: Counter service


        var acceptedCourses = result.getSucceed();
        student.getCourses()
                .addAll(acceptedCourses);
        studentRepository.save(student);

        return result;
    }
}

