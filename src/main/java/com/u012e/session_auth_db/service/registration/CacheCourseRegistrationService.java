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
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Profile("cache")
@RequiredArgsConstructor
@Slf4j
public class CacheCourseRegistrationService implements CourseRegistrationService {
    private final DependencyChecker dependencyChecker;
    private final CourseService courseService;
    private final ParticipantCounterService participantCounterService;
    private final ValueOperations<String, HashSet<Long>> valueOperation;
    private final RegistrationProducer registrationProducer;

    @SafeVarargs
    private <T> Set<T> union(Set<T>... sets) {
        Set<T> flattenedSet = new HashSet<>();
        for (Set<T> set : sets) {
            flattenedSet.addAll(set);
        }
        return flattenedSet;
    }

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
        var courses = getCoursesById(courseIds);

        // Check dependencies
        log.trace("Checking dependencies for student {} and courses {}", student, courses);
        var dependencyCheckResult = dependencyChecker.checkDependencies(student, courses);

        // Check for free slots
        log.trace("Checking for free slots for student {} and courses {}", student, courses);
        var freeSlotResult = registerOnFreeSlots(dependencyCheckResult.getSucceed());

        // Collect failed and accepted courses
        var failedCourses = union(dependencyCheckResult.getFailed(), freeSlotResult.getFailed());
        var acceptedCourses = freeSlotResult.getSucceed();
        log.trace("Student {} succeed with courses: {}", student, acceptedCourses);

        // Finally save
        log.trace("Saving student registration to database {}", student);

        var cacheKey = getKeyOfRegistration(student);

        HashSet<Long> acceptedCourseIds = acceptedCourses.stream()
                .map(Course::getId)
                .collect(Collectors.toCollection(HashSet::new));

        var preRegisteredCourseIds = valueOperation.get(cacheKey);
        valueOperation.set(cacheKey, union(preRegisteredCourseIds, acceptedCourseIds));

        log.trace("Saved student registration to cache {}", student);
        if(valueOperation.get(cacheKey) != null) {
            registrationProducer.addCourses(acceptedCourses, student);
        }

        return RegistrationResult.builder()
                .failed(failedCourses)
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

        var courses = getCoursesById(courseIds);
        var failed = new HashSet<Course>();
        var succeed = new HashSet<Course>();

        for (var course : courses) {
            if (preRegisteredCourseIds.remove(course.getId())) {
                participantCounterService.freeSlot(course);
                succeed.add(course);
            } else {
                failed.add(course);
            }
        }
        valueOperation.set(cacheKey, preRegisteredCourseIds); //re-set registered courses after removing

        if(valueOperation.get(cacheKey) != null) {
            registrationProducer.removeCourses(succeed, student);
        }

        return RegistrationResult.builder()
                .failed(failed)
                .succeed(succeed)
                .build();
    }

    private RegistrationResult registerOnFreeSlots(Set<Course> courses) {
        var ok = new HashSet<Course>();
        var failed = new HashSet<Course>();
        for (var course : courses) {
            if (participantCounterService.isFull(course)) {
                log.warn("Course {} is full", course);
                failed.add(course);
                continue;
            }
            ok.add(course);
            participantCounterService.takeSlot(course);
        }
        return RegistrationResult.builder()
                .failed(failed)
                .succeed(ok)
                .build();
    }

    private HashSet<Course> getCoursesById(List<Long> courseIds) {
        return new HashSet<>(courseService.getAllById(courseIds));
    }
}
