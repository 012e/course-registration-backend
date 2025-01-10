package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.model.Subject;
import com.u012e.session_auth_db.utils.RegistrationResult;

import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface DependencyChecker {
    default RegistrationResult checkDependencies(Student student, Set<Course> courses) {
        Set<Course> ok = new HashSet<>();
        Set<Course> failed = new HashSet<>();

        for (Course course : courses) {
            if (!checkDependency(student, course)) {
                failed.add(course);
            } else {
                ok.add(course);
            }
        }
        return RegistrationResult.builder()
                .succeed(ok)
                .failed(failed)
                .build();
    }

    boolean checkDependency(Student student, Course course);
}
