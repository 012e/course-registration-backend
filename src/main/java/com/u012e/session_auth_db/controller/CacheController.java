package com.u012e.session_auth_db.controller;

import com.u012e.session_auth_db.repository.CourseRepository;
import com.u012e.session_auth_db.repository.StudentRepository;
import com.u012e.session_auth_db.service.CachedCourseService;
import com.u012e.session_auth_db.service.CourseRegistrationBloomFilter;
import com.u012e.session_auth_db.service.registration.CachedRegisteredCoursesService;
import com.u012e.session_auth_db.service.registration.DependencyChecker;
import com.u012e.session_auth_db.service.syncer.ParticipantCounterSyncer;
import com.u012e.session_auth_db.utils.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class CacheController {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final DependencyChecker dependencyChecker;
    private final CachedCourseService cachedCourseService;
    private final ParticipantCounterSyncer participantCounterSyncer;
    private final CourseRegistrationBloomFilter courseRegistrationBloomFilter;
    private final CachedRegisteredCoursesService cachedRegisteredCoursesService;

    @GetMapping("sync/counter")
    public GenericResponse<Object> syncCounter() {
        participantCounterSyncer.sync();
        return GenericResponse.builder()
                .message("synced counter successfully")
                .data(null)
                .build();
    }

    @GetMapping("sync/courses")
    public GenericResponse<Object> syncCourses() {
        cachedCourseService.syncCache();
        return GenericResponse.builder()
                .message("synced courses successfully")
                .data(null)
                .build();
    }

    @GetMapping("sync/registeredCourses")
    public GenericResponse<Object> syncRegisteredCourses() {
        cachedCourseService.syncCache();
        return GenericResponse.builder()
                .message("synced registered courses successfully")
                .data(null)
                .build();
    }

    @GetMapping("sync/courseRegistrations")
    public GenericResponse<Object> syncCourseRegistration() {
        courseRegistrationBloomFilter.init();
        return GenericResponse.builder()
                .message("synced courses successfully")
                .data(null)
                .build();
    }


    @GetMapping("sync/all")
    public GenericResponse<Object> syncAll() {
        participantCounterSyncer.sync();
        cachedCourseService.syncCache();
        courseRegistrationBloomFilter.init();
        cachedRegisteredCoursesService.syncCache();
        return GenericResponse.builder()
                .message("synced all successfully")
                .data(null)
                .build();
    }



    @GetMapping("prepareCourses")
    public GenericResponse<Object> prepareCourseCaching() {
        var students = studentRepository.findAll();
        var top10Courses = new HashSet<>(courseRepository.findTop10ByOrderByIdAsc());
        for (var student : students) {
            dependencyChecker.checkDependencies(student, top10Courses);
        }
        return GenericResponse.builder()
                .message("prepared course successfully")
                .data(null)
                .build();
    }
}
