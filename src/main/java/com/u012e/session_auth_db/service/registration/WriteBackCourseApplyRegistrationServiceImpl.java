package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.configuration.CacheConfiguration;
import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.queue.registration.RegistrationProducer;
import com.u012e.session_auth_db.utils.RegistrationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Profile("cache")
@RequiredArgsConstructor
@Slf4j
public class WriteBackCourseApplyRegistrationServiceImpl implements CourseApplyRegistrationService {
    private final ValueOperations<String, HashSet<Long>> valueOperation;
    private final RegistrationProducer registrationProducer;
    private final CourseRegistrationService courseRegistrationService;

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
    public RegistrationResult register(Student student, List<Long> courseIds) {
        var registrationResult = courseRegistrationService.register(student, courseIds);
        var acceptedCourses = registrationResult.getSucceed();

        var cacheKey = getKeyOfRegistration(student);

        HashSet<Long> acceptedCourseIds = acceptedCourses.stream()
                .map(Course::getId)
                .collect(Collectors.toCollection(HashSet::new));

        var preRegisteredCourseIds = valueOperation.get(cacheKey);
        log.trace("Saved student registration to cache {}", cacheKey);
        valueOperation.set(cacheKey, union(preRegisteredCourseIds, acceptedCourseIds));

        if (!Objects.equals(preRegisteredCourseIds, valueOperation.get(cacheKey))) {
            registrationProducer.addCourses(acceptedCourses, student);
        }

        return RegistrationResult.builder()
                .failed(registrationResult.getFailed())
                .succeed(acceptedCourses)
                .build();
    }

    @Override
    public RegistrationResult unregister(Student student, List<Long> courseIds) {
        var cacheKey = getKeyOfRegistration(student);
        var preRegisteredCourseIds = valueOperation.get(cacheKey);

        if (preRegisteredCourseIds == null) {
            throw new IllegalArgumentException("Student has not registered any courses yet.");
        }

        var preRegisteredCourses = courseRegistrationService.getCoursesById(preRegisteredCourseIds.stream().toList());

        var registrationResult = courseRegistrationService.unregister(student, courseIds, preRegisteredCourses);

        var acceptedCourses = registrationResult.getSucceed();

        // preRegisteredCourses is the courses that has not unregistered successfully, so we update it back to cache.
        preRegisteredCourseIds = preRegisteredCourses.stream().map(Course::getId).collect(Collectors.toCollection(HashSet::new));

        valueOperation.set(cacheKey, preRegisteredCourseIds); //re-set registered courses after removing

        if (!Objects.equals(valueOperation.get(cacheKey), preRegisteredCourseIds)) {
            // remove all courses that is valid to be unregistered
            registrationProducer.removeCourses(acceptedCourses, student);
        }

        return RegistrationResult.builder()
                .failed(registrationResult.getFailed())
                .succeed(acceptedCourses)
                .build();
    }

}

