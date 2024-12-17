package com.u012e.session_auth_db;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.model.Subject;
import com.u012e.session_auth_db.repository.StudentRepository;
import com.u012e.session_auth_db.service.CourseService;
import com.u012e.session_auth_db.service.registration.CourseRegistrationServiceImpl;
import com.u012e.session_auth_db.service.registration.DatabaseParticipantCounterService;
import com.u012e.session_auth_db.service.registration.DependencyChecker;
import com.u012e.session_auth_db.utils.RegistrationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {
    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseService courseService;

    @Mock
    private DependencyChecker dependencyChecker;

    @Mock
    private DatabaseParticipantCounterService participantCounterService;

    @InjectMocks
    private CourseRegistrationServiceImpl registrationService;

    @Test
    void StudentRegisterEmptyCourse() {
        var student = Student.builder()
                .lastName("Doe")
                .firstName("John")
                .results(new HashSet<>())
                .courses(new HashSet<>())
                .build();

        var courses = new ArrayList<Long>();

        when(courseService.getAllById(courses))
                .thenReturn(new ArrayList<>());
        when(dependencyChecker.checkDependencies(new HashSet<>(), new HashSet<>()))
                .thenReturn(RegistrationResult.builder()
                        .failed(new HashSet<>())
                        .succeed(new HashSet<>())
                        .build());
//        when(participantCounterService.isFull(any()))
//                .thenReturn(false);
        verify(participantCounterService, never())
                .isFull(any());


        var result = registrationService.register(student, courses);
        verify(studentRepository, times(1))
                .save(student);
        var expected = new RegistrationResult();

        assertEquals(result, expected, "Registration result is not as expected");
    }

    @Test
    void StudentRegisterCourses() {
        var student = Student.builder()
                .lastName("Doe")
                .firstName("John")
                .results(new HashSet<>())
                .courses(new HashSet<>())
                .build();

        var courseIds = new ArrayList<Long>();
        courseIds.add(1L);
        courseIds.add(2L);
        courseIds.add(3L);

        var subjects = new ArrayList<Subject>();
        subjects.add(Subject.builder()
                .id(1L)
                .name("Math")
                .build());
        subjects.add(Subject.builder()
                .id(2L)
                .name("Yayaya")
                .build());
        subjects.add(Subject.builder()
                .id(3L)
                .name("funny")
                .build());

        var courses = new ArrayList<Course>();
        courses.add(Course.builder()
                .id(1L)
                .subject(subjects.get(0))
                .build());
        courses.add(Course.builder()
                .id(2L)
                .subject(subjects.get(1))
                .build());
        courses.add(Course.builder()
                .id(3L)
                .subject(subjects.get(2))
                .build());

        var courseSet = new HashSet<>(courses);

        when(courseService.getAllById(courseIds))
                .thenReturn(courses);
        when(dependencyChecker.checkDependencies(courseSet, new HashSet<>()))
                .thenReturn(RegistrationResult.builder()
                        .succeed(courseSet)
                        .failed(new HashSet<>())
                        .build());
        when(participantCounterService.isFull(any()))
                .thenReturn(false);

        var result = registrationService.register(student, courseIds);
        verify(studentRepository, times(1))
                .save(student);
        var expected = RegistrationResult.builder()
                .succeed(courseSet)
                .failed(new HashSet<>())
                .build();

        assertEquals(result, expected, "Registration result is not as expected");
    }
}
