package com.u012e.session_auth_db.service.seeder;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Subject;
import com.u012e.session_auth_db.repository.CourseRepository;
import com.u012e.session_auth_db.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseSeeder implements Seeder<Course> {
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final Faker faker;

    private Long totalSubjects;

    private Subject getRandomSubject() {
        return subjectRepository
                .findById(faker.number().numberBetween(1, totalSubjects))
                .get();
    }

    @Override
    public void seed(int count) {
        var courses = new ArrayList<Course>(count);

        // WARN: possible missing subjects
        totalSubjects = subjectRepository.count();

        log.trace("Seeding {} courses", count);
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
            course.setSubject(getRandomSubject());
            courses.add(course);
        }
        log.trace("Finished seeding {} courses", count);
        courseRepository.saveAll(courses);
    }
}
