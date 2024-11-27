package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Subject;
import com.u012e.session_auth_db.utils.RegistrationResult;

import java.util.HashSet;
import java.util.Set;

public interface DependencyChecker {
    default RegistrationResult checkDependencies(Set<Course> courses, Set<Subject> learnedSubjects) {
        Set<Course> ok = new HashSet<>();
        Set<Course> failed = new HashSet<>();
        for (Course course : courses) {
            if (!checkDependency(course, learnedSubjects)) {
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

    boolean checkDependency(Course course, Set<Subject> learnedSubjects);
}
