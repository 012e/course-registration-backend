package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.model.Subject;
import com.u012e.session_auth_db.repository.StudentRepository;
import com.u012e.session_auth_db.service.CourseService;
import com.u012e.session_auth_db.utils.RegistrationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseCourseRegistrationService implements CourseRegistrationService {
    private final DependencyChecker dependencyChecker;
    private final CourseService courseService;
    private final StudentRepository studentRepository;
    private final ParticipantCounterService participantCounterService;

    @SafeVarargs
    public static <T> Set<T> flatten(Set<T>... sets) {
        Set<T> flattenedSet = new HashSet<>();
        for (Set<T> set : sets) {
            flattenedSet.addAll(set);
        }
        return flattenedSet;
    }

    private static Set<Subject> getLearnedSubjects(Student student) {
        var learnedSubjects = student
                .getResults()
                .parallelStream()
                .map(result -> result.getCourse()
                        .getSubject())
                .collect(Collectors.toSet());
        return learnedSubjects;
    }

    @Override
    public RegistrationResult register(Student student, List<Long> courseIds) {
        var courses = getCoursesById(courseIds);
        var learnedSubjects = getLearnedSubjects(student);

        log.trace("Checking dependencies for student {} and courses {}", student, courses);

        // Check dependencies
        var dependencyCheckResult = dependencyChecker.checkDependencies(courses, learnedSubjects);
        var freeSlotResult = registerOnFreeSlots(dependencyCheckResult.getSucceed());

        // Collect failed and accepted courses
        var failedCourses = flatten(dependencyCheckResult.getFailed(), freeSlotResult.getFailed());
        var acceptedCourses = freeSlotResult.getSucceed();

        // Finally save
        student
                .getCourses()
                .addAll(acceptedCourses);
        studentRepository.save(student);

        return RegistrationResult.builder()
                .failed(failedCourses)
                .succeed(acceptedCourses)
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
        var courses = new HashSet<>(courseService.getAllById(courseIds));
        return courses;
    }
}
