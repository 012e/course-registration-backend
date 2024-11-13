package com.u012e.session_auth_db.service.seeder;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Result;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.repository.CourseRepository;
import com.u012e.session_auth_db.repository.ResultRepository;
import com.u012e.session_auth_db.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResultSeeder implements Seeder<Result> {
    private final ResultRepository resultRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final Faker faker;
    private final Random random;

    private Long courseCount;

    private List<Course> getRandomCourses(int count) {
        var randomCourses = new ArrayList<Course>(count);
        @SuppressWarnings("unchecked") var courseIds = random
                .longs(1, courseCount)
                .distinct()
                .limit(count)
                .boxed()
                .collect(Collectors.toList());
        return courseRepository.findAllById(courseIds);
    }

    @Override
    public void seed(int count) {
        var results = new ArrayList<Result>(count);

        // WARN: possible missing courses
        courseCount = courseRepository.count();

        for (Student student : studentRepository.findAll()) {
            var randomCourses = getRandomCourses(faker.number().numberBetween(1, count));
            for (Course course : randomCourses) {
                results.add(Result.builder()
                        .student(student)
                        .course(course)
                        .passed(faker.bool().bool())
                        .build());
            }
        }

        resultRepository.saveAll(results);
    }
}
