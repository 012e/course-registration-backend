package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.configuration.CacheConfiguration;
import com.u012e.session_auth_db.model.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@Profile("cache")
@RequiredArgsConstructor
@Slf4j
public class CacheParticipantCounterService implements ParticipantCounterService {
    private final ValueOperations<String, Integer> valueOperation;

    private String getKeyOfCount(Course course) {
        return String.format("%s:%d", CacheConfiguration.PARTICIPANT_CACHE, course.getId());
    }

    private String getKeyOfLimit(Course course) {
        return String.format("%s:%d:limit", CacheConfiguration.PARTICIPANT_CACHE, course.getId());
    }

    @Override
    public void takeSlot(Course course) {
        final var key = getKeyOfCount(course);
        log.trace("Incrementing key: {}", key);
        valueOperation.increment(key);
    }

    @Override
    public void freeSlot(Course course) {
        final var key = getKeyOfCount(course);
        log.trace("Decrementing key: {}", key);
        valueOperation.decrement(key);
    }

    @Override
    public boolean isFull(Course course) {
        final var value = getCount(course);
        final var limit = getLimit(course);

        return value >= limit;
    }

    private Long getValue(String key) {
        var value = valueOperation.get(key);
        if (value == null) {
            log.trace("Value not found for key: {}, defaulting to 0", key);
            return 0L;
        }
        log.trace("Value found for key: {}, value: {}", key, value);
        return value.longValue();
    }

    private Long getLimit(Course course) {
        final var key = getKeyOfLimit(course);
        return getValue(key);
    }

    // TODO: is there any race condition?
    private Long getCount(Course course) {
        final var key = getKeyOfCount(course);
        return getValue(key);
    }
}
