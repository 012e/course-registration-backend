package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.configuration.CacheConfiguration;
import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.queue.registration.RegistrationProducer;
import com.u012e.session_auth_db.repository.CourseRepository;
import com.u012e.session_auth_db.repository.StudentRepository;
import com.u012e.session_auth_db.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CachedRegisteredCoursesService {
    private final ValueOperations<String, HashSet<Long>> valueOperation;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    private String getKeyOfRegistration(Long studentId) {
        return String.format("%s:%d", CacheConfiguration.REGISTRATION_CACHE, studentId);
    }

    public Set<Long> getRegisteredCourses(Long studentId) {
        var cacheKey = getKeyOfRegistration(studentId);
        var savedCourseIds = valueOperation.get(cacheKey);
        if (savedCourseIds == null) {
            throw new IllegalArgumentException("Student has not registered any courses yet.");
        }
        return savedCourseIds;
    }

    public void removeFromCache(Long studentId, Set<Long> courseIdsToRemove) {
        var cacheKey = getKeyOfRegistration(studentId);
        var savedCourseIds = valueOperation.get(cacheKey);
        if (savedCourseIds == null) {
            throw new IllegalArgumentException("Student has not registered any courses yet.");
        }
        savedCourseIds.removeAll(courseIdsToRemove);
        valueOperation.set(cacheKey, savedCourseIds);
    }

    public void saveToCache(Long studentId, Set<Long> acceptedCourseIds) {
        var cacheKey = getKeyOfRegistration(studentId);

        valueOperation.setIfAbsent(cacheKey, new HashSet<>());
        var savedCourseIds = valueOperation.get(cacheKey);
        if (savedCourseIds == null) {
            throw new IllegalArgumentException("Course can't be null");
        }
        savedCourseIds.addAll(acceptedCourseIds);
        valueOperation.set(cacheKey, savedCourseIds);
    }

    public void syncCache() {
        var students = studentRepository.findAll();
        students.parallelStream()
                .forEach(student -> {
                    courseRepository.findByStudents(student);
                    var courseIds = courseRepository.findByStudents(student)
                            .parallelStream()
                            .map(Course::getId)
                            .collect(Collectors.toSet());
                    saveToCache(student.getId(), courseIds);
                });
    }
}
