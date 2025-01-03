package com.u012e.session_auth_db.controller;

import com.u012e.session_auth_db.repository.CourseRepository;
import com.u012e.session_auth_db.repository.StudentRepository;
import com.u012e.session_auth_db.service.registration.DatabaseDependencyChecker;
import com.u012e.session_auth_db.service.registration.DependencyChecker;
import com.u012e.session_auth_db.service.syncer.ParticipantCounterSyncer;
import com.u012e.session_auth_db.utils.BloomFilterManager;
import com.u012e.session_auth_db.utils.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class CacheController {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final DependencyChecker dependencyChecker;
    private final ParticipantCounterSyncer participantCounterSyncer;
    private final DatabaseDependencyChecker databaseDependencyChecker;

    @GetMapping("sync/counter")
    public GenericResponse<Object> syncCounter() {
        participantCounterSyncer.sync();
        return GenericResponse.builder()
                .message("synced counter successfully")
                .data(null)
                .build();
    }

    @GetMapping("prepareCourses")
    public GenericResponse<Object> prepareCourseCaching() {
        var students = studentRepository.findAll();
        var courses = courseRepository.findAll();
        for (var student : students) {
            for (var course: courses) {
                String value = BloomFilterManager.getValue(student, course);
                boolean dependencyResult = databaseDependencyChecker.checkDependency(student, course);
                if (dependencyResult){
                    BloomFilterManager.main.put(value);
                }
                if (!BloomFilterManager.main.mightContain(value)){
                    continue;
                }
                if (!dependencyResult){
                    BloomFilterManager.backup.put(value);
                }
            }
        }

        return GenericResponse.builder()
                .message("prepared course successfully")
                .data(null)
                .build();

    }
}
