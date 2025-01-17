package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.repository.CourseRepository;
import com.u012e.session_auth_db.service.CourseService;
import com.u012e.session_auth_db.utils.RegistrationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class CourseRegistrationServiceImpl implements CourseRegistrationService {
    private final DependencyChecker dependencyChecker;
    private final CourseService courseService;
    private final ParticipantCounterService participantCounterService;
    private final CourseApplyRegistrationService courseApplyRegistrationService;

    @Autowired(required = false)
    private CachedRegisteredCoursesService cachedRegisteredCoursesService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    private CourseRepository courseRepository;


    public CourseRegistrationServiceImpl(
            DependencyChecker dependencyChecker,
            CourseService courseService,
            ParticipantCounterService participantCounterService,
            CourseApplyRegistrationService courseApplyRegistrationService
    ) {
        this.dependencyChecker = dependencyChecker;
        this.courseService = courseService;
        this.participantCounterService = participantCounterService;
        this.courseApplyRegistrationService = courseApplyRegistrationService;
    }

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

        // Check registered courses
        log.trace("Checking registered courses for student {}", student);
        markDuplicateAsFailed(student, courses, dependencyCheckResult);

        // Check for free slots
        log.trace("Checking for free slots for student {} and courses {}", student, courses);
        var freeSlotResult = registerOnFreeSlots(dependencyCheckResult.getSucceed());

        // Collect failed and accepted courses
        var failedCourses = union(dependencyCheckResult.getFailed(), freeSlotResult.getFailed());
        var acceptedCourses = freeSlotResult.getSucceed();
        log.trace("Student {} succeed with courses: {}", student, acceptedCourses);

        // Finally save
        log.trace("Saving student registration to database {}", student);
        courseApplyRegistrationService.applyRegistration(student, acceptedCourses);
        log.trace("Saved student registration to database {}", student);


        return RegistrationResult.builder()
                .failed(failedCourses)
                .succeed(acceptedCourses)
                .build();
    }

    private void markDuplicateAsFailed(Student student, HashSet<Course> courses, RegistrationResult dependencyCheckResult) {
        if (activeProfile.equals("cache")) {
            markDuplicateAsFailedCache(student, courses, dependencyCheckResult);
            return;
        }
        var registeredCourses = courseRepository.findByStudents(student);
        // Intersection of registered courses and courses
        registeredCourses.retainAll(courses);
        dependencyCheckResult.getFailed()
                .addAll(registeredCourses);
        dependencyCheckResult.getSucceed()
                .removeAll(registeredCourses);
    }

    private void markDuplicateAsFailedCache(Student student, HashSet<Course> courses, RegistrationResult dependencyCheckResult) {
        var registerCourseIds = cachedRegisteredCoursesService.getRegisteredCourses(student.getId());
        var duplicateCourses = new HashSet<Course>();
        for (var course : courses) {
            if (registerCourseIds.contains(course.getId())) {
                duplicateCourses.add(course);
            }
        }
        dependencyCheckResult.getFailed()
                .addAll(duplicateCourses);
        dependencyCheckResult.getSucceed()
                .removeAll(duplicateCourses);
    }

    @Override
    public RegistrationResult unregister(Student student, List<Long> courseIds) {
        var courses = getCoursesById(courseIds);
        var oldCourses = student.getCourses();

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

        courseApplyRegistrationService.removeRegistration(student, succeed);

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

