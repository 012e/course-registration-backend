package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Subject;
import com.u012e.session_auth_db.service.DependencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DependencyExtractor {
    private final DependencyService dependencyService;

    public Set<Subject> getDependantSubjectsRecursively(Subject subject) {
        var result = new HashSet<Subject>();
        if (subject == null) {
            return result;
        }
        log.trace("Extracting dependencies for subject {}", subject);

        var deps = dependencyService.getDependencies(subject);
        if (deps == null) {
            return result;
        }
        log.trace("Found {} dependencies for subject {}", deps.size(), subject);

        for (var dep : deps) {
            log.trace("Extracting dependencies for dependency {}", dep);
            result.add(dep);
            result.addAll(getDependantSubjectsRecursively(dep));
        }
        return result;
    }

    public Set<Subject> getDependantSubjectsRecursively(Course course) {
        log.trace("Extracting dependencies for course {}", course);
        var subject = course.getSubject();
        return getDependantSubjectsRecursively(subject);
    }
}
