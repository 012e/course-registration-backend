package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.configuration.CacheConfiguration;
import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.queue.counter.CounterProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile("cache")
@RequiredArgsConstructor
@Slf4j
public class CacheParticipantCounterService implements ParticipantCounterService {
    private final ValueOperations<String, Integer> valueOperation;
    private final CounterProducer counterProducer;

    private String getKeyOfCount(Course course) {
        return String.format("%s:%d", CacheConfiguration.PARTICIPANT_CACHE, course.getId());
    }

    private String getKeyOfCount(Long courseId) {
        return String.format("%s:%d", CacheConfiguration.PARTICIPANT_CACHE, courseId);
    }

    private String getKeyOfLimit(Course course) {
        return String.format("%s:%d:limit", CacheConfiguration.PARTICIPANT_CACHE, course.getId());
    }

    @Override
    public void takeSlot(Course course) {
        final var key = getKeyOfCount(course);
        log.trace("Incrementing key: {}", key);
        valueOperation.increment(key);
        counterProducer.increase(course);
    }

    @Override
    public void freeSlot(Course course) {
        final var key = getKeyOfCount(course);
        log.trace("Decrementing key: {}", key);
        valueOperation.decrement(key);
        counterProducer.decrease(course);
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
    public int getCount(Course course) {
        final var key = getKeyOfCount(course);
        return Math.toIntExact(getValue(key));
    }

    @Override
    public int getCount(Long courseId) {
        final var key = getKeyOfCount(courseId);
        return Math.toIntExact(getValue(key));
    }

    @Override
    public List<Integer> getCounts(List<Long> courseIds) {
        var keys = new ArrayList<>(courseIds).parallelStream()
                .map(this::getKeyOfCount)
                .toList();
        var results = valueOperation.multiGet(keys);
        return results.parallelStream()
                .map(value -> value == null ? 0 : value)
                .map(Number::intValue)
                .toList();
    }

}
