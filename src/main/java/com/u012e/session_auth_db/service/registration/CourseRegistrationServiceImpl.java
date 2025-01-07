package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.service.CourseService;
import com.u012e.session_auth_db.utils.RegistrationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseRegistrationServiceImpl implements CourseRegistrationService {
    private final DependencyChecker dependencyChecker;
    private final CourseService courseService;
    private final ParticipantCounterService participantCounterService;

    @SafeVarargs
    public static <T> Set<T> union(Set<T>... sets) {
        Set<T> flattenedSet = new HashSet<>();
        for (Set<T> set : sets) {
            flattenedSet.addAll(set);
        }
        return flattenedSet;
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

        return RegistrationResult.builder()
                .failed(failedCourses)
                .succeed(acceptedCourses)
                .build();
    }

    @Override
    public RegistrationResult unregister(Student student, List<Long> courseIds, Set<Course> oldCourses) {
        var courses = getCoursesById(courseIds);

        var failed = new HashSet<Course>();
        var succeed = new HashSet<Course>();

        for (var course : courses) {
            if (oldCourses.remove(course)) {
                participantCounterService.freeSlot(course);
                succeed.add(course);
            } else {
                failed.add(course);
            }
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
            } else {
                ok.add(course);
                participantCounterService.takeSlot(course);
            }
        }

        return RegistrationResult.builder()
                .failed(failed)
                .succeed(ok)
                .build();
    }

    public HashSet<Course> getCoursesById(List<Long> courseIds) {
        return new HashSet<>(courseService.getAllById(courseIds));
    }
}

