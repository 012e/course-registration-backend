package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Subject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseDependencyChecker implements DependencyChecker {
    private final DependencyExtractor dependencyExtractor;

    @Cacheable("dependencyChecker")
    @Override
    public boolean checkDependency(Course course, Set<Subject> learnedSubjects) {
        return learnedSubjects.containsAll(
                dependencyExtractor.getDependantSubjectsRecursively(course)
        );
    }
}
