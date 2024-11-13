package com.u012e.session_auth_db.service.seeder;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseSeeder implements Seeder<Course> {
    private final CourseRepository courseRepository;
    private final Faker faker;
    private final List<Course> courseList;

    @Override
    public void seed(int count) {
        for (int i = 0; i < count; ++i) {
            Course course = new Course();
            int shift = faker.number().numberBetween(1, 2);
            if (shift == 1) {
                course.setStartPeriod(faker.number().numberBetween(1, 2));
            } else {
                course.setStartPeriod(faker.number().numberBetween(6, 7));
            }
            course.setEndPeriod(course.getStartPeriod() + 3);
            course.setDayOfWeek(faker.number().numberBetween(1, 6));
            course.setMaxParticipants(faker.number().numberBetween(10, 50));
        }
    }
}
