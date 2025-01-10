package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.model.Subject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class DatabaseDependencyChecker implements DependencyChecker {
    private final DependencyExtractor dependencyExtractor;

    private Set<Subject> getLearnedSubjects(Student student) {
        return student
                .getResults()
                .parallelStream()
                .map(result -> result.getCourse()
                        .getSubject())
                .collect(Collectors.toSet());
    }


    @Override
    public boolean checkDependency(Student student, Course course) {
        final var learnedSubjects = getLearnedSubjects(student);
        log.trace("student {} learned subjects: {}", student, learnedSubjects);
        log.trace("Checking dependencies for student {} and course {}", student, course);
        return learnedSubjects.containsAll(
                dependencyExtractor.getDependantSubjectsRecursively(course)
        );
    }
}
