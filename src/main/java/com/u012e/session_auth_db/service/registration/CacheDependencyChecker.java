package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.configuration.CacheConfiguration;
import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.service.CourseRegistrationBloomFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("cache")
@RequiredArgsConstructor
public class CacheDependencyChecker implements DependencyChecker {
    private final CourseRegistrationBloomFilter courseRegistrationBloomFilter;

    private static String getValue(Student student, Course course) {
        return String.format("%s:%d:%d",
                CacheConfiguration.DEPENDENCY_CACHE,
                student.getId(),
                course.getId());
    }

    @Override
    public boolean checkDependency(Student student, Course course) {
        return courseRegistrationBloomFilter.contains(student, course);
    }
}
