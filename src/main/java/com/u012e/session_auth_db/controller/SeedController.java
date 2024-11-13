package com.u012e.session_auth_db.controller;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.model.Subject;
import com.u012e.session_auth_db.service.seeder.Seeder;
import com.u012e.session_auth_db.utils.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("seed")
@RequiredArgsConstructor
public class SeedController {

    private final Seeder<Student> studentSeeder;
    private final Seeder<Subject> subjectSeeder;
    private final Seeder<Course> courseSeeder;

    @GetMapping("students")
    public GenericResponse<String> seedStudents(@RequestParam(defaultValue = "100") int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        studentSeeder.seed(count);
        return GenericResponse.<String>builder()
                .message("Students seeded")
                .data(null)
                .success(true)
                .build();
    }
    @GetMapping("subjects")
    public GenericResponse<String> seedSubjects(@RequestParam(defaultValue = "100") int count){
        if (count < 0) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        subjectSeeder.seed(count);
        return GenericResponse.<String>builder()
                .message("Subjects seeded")
                .data(null)
                .success(true)
                .build();
    }
    @GetMapping("courses")
    public GenericResponse<String> seedCourses(@RequestParam(defaultValue = "50") int count){
        if (count < 0) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        courseSeeder.seed(count);
        return GenericResponse.<String>builder()
                .message("Courses seeded")
                .data(null)
                .success(true)
                .build();
    }
}
