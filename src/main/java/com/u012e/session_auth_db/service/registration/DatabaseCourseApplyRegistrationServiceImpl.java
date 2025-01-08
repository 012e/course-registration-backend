package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.repository.StudentRepository;
import com.u012e.session_auth_db.service.CourseService;
import com.u012e.session_auth_db.utils.RegistrationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Profile("database")
@Primary
@RequiredArgsConstructor
@Slf4j
public class DatabaseCourseApplyRegistrationServiceImpl implements CourseApplyRegistrationService {
    private final StudentRepository studentRepository;

    @Override
    public void applyRegistration(Student student, Set<Course> courses) {
        log.trace("Saving student registration to database {}", student);
        var oldCourses = student.getCourses();
        oldCourses.addAll(courses);
        studentRepository.save(student);
        log.trace("Saved student registration to database {}", student);

    }

    @Override
    public void removeRegistration(Student student, Set<Course> courses) {
        var currentCourses = student.getCourses();
        currentCourses.removeIf(courses::contains);
        studentRepository.save(student);
    }
}