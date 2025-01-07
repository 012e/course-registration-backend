package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.repository.StudentRepository;
import com.u012e.session_auth_db.utils.RegistrationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("database")
@RequiredArgsConstructor
@Slf4j
public class DatabaseCourseApplyRegistrationServiceImpl implements CourseApplyRegistrationService {
    private final CourseRegistrationService courseRegistrationService;
    private final StudentRepository studentRepository;

    @Override
    public RegistrationResult register(Student student, List<Long> courseIds) {
        var registrationResult = courseRegistrationService.register(student, courseIds);
        var acceptedCourses = registrationResult.getSucceed();
        var failedCourses = registrationResult.getFailed();

        // Finally save
        log.trace("Saving student registration to database {}", student);
        var oldCourses = student.getCourses();
        oldCourses.addAll(acceptedCourses);
        student.setCourses(oldCourses);
        studentRepository.save(student);
        log.trace("Saved student registration to database {}", student);

        return RegistrationResult.builder()
                .failed(failedCourses)
                .succeed(acceptedCourses)
                .build();
    }

    @Override
    public RegistrationResult unregister(Student student, List<Long> courseIds) {
        var oldCourses = student.getCourses();
        var registrationResult = courseRegistrationService.unregister(student, courseIds, oldCourses);

        student.setCourses(oldCourses);
        studentRepository.save(student);

        return RegistrationResult.builder()
                .failed(registrationResult.getFailed())
                .succeed(registrationResult.getSucceed())
                .build();
    }
}