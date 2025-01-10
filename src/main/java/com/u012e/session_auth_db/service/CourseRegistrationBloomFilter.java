package com.u012e.session_auth_db.service;

import com.u012e.session_auth_db.configuration.CacheConfiguration;
import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.repository.CourseRepository;
import com.u012e.session_auth_db.repository.StudentRepository;
import com.u012e.session_auth_db.service.registration.DatabaseDependencyChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.UnifiedJedis;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseRegistrationBloomFilter {
    private final UnifiedJedis jedis;
    private final DatabaseDependencyChecker databaseDependencyChecker;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    // 10000 students, 1000 courses => capacity = 10000 * 1000 = 10000000 at most, which is about 11MB
    private final int TOTAL_CAPACITY = 10_000_000;

    // capacity is 1% of the main cache
    private final int BACKUP_CAPACITY = 100_000;

    private static String getValue(Student student, Course course) {
        return String.format("%s:%d:%d",
                CacheConfiguration.DEPENDENCY_CACHE,
                student.getId(),
                course.getId());
    }

    private static String getValue(Long studentId, Long courseId) {
        return String.format("%s:%d:%d",
                CacheConfiguration.DEPENDENCY_CACHE,
                studentId,
                courseId);
    }

    public void init() {
        log.info("Initializing course registration bloom filter");
        jedis.del(CacheConfiguration.DEPENDENCY_CACHE);
        jedis.bfReserve(CacheConfiguration.DEPENDENCY_CACHE, 0.01, TOTAL_CAPACITY);

        // backup cache
        jedis.del(CacheConfiguration.DEPENDENCY_BACKUP_CACHE);
        jedis.bfReserve(CacheConfiguration.DEPENDENCY_BACKUP_CACHE, 0.01, BACKUP_CAPACITY);

        var allStudents = studentRepository.findAll();
        var allCourses = courseRepository.findAll();
        allStudents.parallelStream().forEach(student -> {
            allCourses.parallelStream().forEach(course -> {
                String value = getValue(student, course);
                jedis.bfAdd(CacheConfiguration.DEPENDENCY_CACHE, value);
            });
        });

        allStudents.forEach(student -> {
            allCourses.forEach(course -> {
                String value = getValue(student, course);
                boolean dependencyResult = databaseDependencyChecker.checkDependency(student, course);
                if (jedis.bfExists(CacheConfiguration.DEPENDENCY_CACHE, value) && !dependencyResult){
                    jedis.bfAdd(CacheConfiguration.DEPENDENCY_BACKUP_CACHE, value);
                }
            });
        });
    }

    public boolean contains(Student student, Course course) {
        String value = getValue(student, course);
        log.trace("Checking dependency for student {} and course {}", student, course);
        if (!jedis.bfExists(CacheConfiguration.DEPENDENCY_CACHE, value)){
            log.trace("Dependency not found in bloom filter for student {} and course {}", student, course);
            return false;
        }
        if (!jedis.bfExists(CacheConfiguration.DEPENDENCY_BACKUP_CACHE, value)){
            log.trace("Dependency not found in backup bloom filter for student {} and course {}", student, course);
            return true;
        }
        log.trace("Dependency found in backup bloom filter for student {} and course {}", student, course);
        return databaseDependencyChecker.checkDependency(student, course);
    }
}
