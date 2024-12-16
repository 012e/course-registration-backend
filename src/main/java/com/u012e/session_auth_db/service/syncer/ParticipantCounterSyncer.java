package com.u012e.session_auth_db.service.syncer;

import com.u012e.session_auth_db.configuration.CacheConfiguration;
import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantCounterSyncer {
    private final ValueOperations<String, Integer> redisValueOperations;
    private final CourseRepository courseRepository;

    private String getKeyLimit(Course course) {
        return String.format("%s:%d:limit", CacheConfiguration.PARTICIPANT_CACHE, course.getId());
    }

    // TODO: sync total participant
    @Transactional
    public void sync() {
        syncLimit();
    }

    private void syncLimit() {
        courseRepository.findAllBy()
                .forEach(course -> {
                    final var key = getKeyLimit(course);
                    final var count = course.getMaxParticipants();
                    redisValueOperations.set(key, count);
                });
    }
}
