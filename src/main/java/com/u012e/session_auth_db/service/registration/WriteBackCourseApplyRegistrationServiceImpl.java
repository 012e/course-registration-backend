package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.configuration.CacheConfiguration;
import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.queue.registration.RegistrationProducer;
import com.u012e.session_auth_db.service.CourseService;
import com.u012e.session_auth_db.utils.RegistrationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Profile("cache")
@RequiredArgsConstructor
@Slf4j
public class WriteBackCourseApplyRegistrationServiceImpl implements CourseApplyRegistrationService {
    private final ValueOperations<String, HashSet<Long>> valueOperation;
    private final RedisTemplate<String, HashSet<Long>> redisTemplate;
    private final RegistrationProducer registrationProducer;
    private final CourseService courseService;

    @SafeVarargs
    private <T> HashSet<T> union(HashSet<T>... sets) {
        HashSet<T> flattenedSet = new HashSet<>();
        for (HashSet<T> set : sets) {
            flattenedSet.addAll(set);
        }
        return flattenedSet;
    }

    private String getKeyOfRegistration(Student student) {
        return String.format("%s:%d", CacheConfiguration.REGISTRATION_CACHE, student.getId());
    }

    @Override
    public void applyRegistration(Student student, Set<Course> courses) {
        var cacheKey = getKeyOfRegistration(student);
        var acceptedCourseIds = courses.stream()
                .map(Course::getId)
                .collect(Collectors.toSet());

        if (!redisTemplate.hasKey(cacheKey)) {
            valueOperation.set(cacheKey, new HashSet<>());
        }

        var savedCourseIds = valueOperation.get(cacheKey);
        if (savedCourseIds == null) {
            throw new IllegalArgumentException("Course can't be null");
        }
        savedCourseIds.addAll(acceptedCourseIds);
        valueOperation.set(cacheKey, savedCourseIds);

        var newCourses = courseService.getCourseByIds(new ArrayList<>(acceptedCourseIds));

        registrationProducer.addCourses(new HashSet<>(newCourses), student);
    }

    @Override
    public void removeRegistration(Student student, Set<Course> courses) {
        var cacheKey = getKeyOfRegistration(student);
        var savedCourseIds = valueOperation.get(cacheKey);
        if (savedCourseIds == null) {
            throw new IllegalArgumentException("Student has not registered any courses yet.");
        }
        var courseIdsToRemove = courses.stream().map(Course::getId).collect(Collectors.toSet());
        savedCourseIds.removeAll(courseIdsToRemove);
        valueOperation.set(cacheKey, savedCourseIds);
        registrationProducer.removeCourses(courses, student);
    }

}