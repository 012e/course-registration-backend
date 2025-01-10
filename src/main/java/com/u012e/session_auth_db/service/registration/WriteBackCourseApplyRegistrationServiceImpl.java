package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.queue.registration.RegistrationProducer;
import com.u012e.session_auth_db.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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
    private final CachedRegisteredCoursesService cachedRegisteredCoursesService;

    @Override
    public void applyRegistration(Student student, Set<Course> courses) {
        var acceptedCourseIds = courses.stream()
                .map(Course::getId)
                .collect(Collectors.toSet());

        cachedRegisteredCoursesService.saveToCache(student.getId(), acceptedCourseIds);
       registrationProducer.addCourses(acceptedCourseIds, student);
    }

    @Override
    public void removeRegistration(Student student, Set<Course> courses) {
        var courseIdsToRemove = courses.stream()
                .map(Course::getId)
                .collect(Collectors.toSet());
        cachedRegisteredCoursesService.removeFromCache(student.getId(), courseIdsToRemove);
        registrationProducer.removeCourses(courseIdsToRemove, student);
    }


}