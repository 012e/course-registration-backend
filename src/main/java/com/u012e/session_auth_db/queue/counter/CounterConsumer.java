package com.u012e.session_auth_db.queue.counter;

import com.u012e.session_auth_db.queue.counter.dto.UpdateCounterDto;
import com.u012e.session_auth_db.repository.CourseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@RabbitListener(queues = "counter")
public class CounterConsumer {
    private final CourseRepository courseRepository;

    @RabbitListener(queues = "counter")
    public void consume(@Payload List<@Valid UpdateCounterDto> dtos) {
        log.trace(" [x] Received '{}'", dtos);
        Map<Long, Integer> countDiffs = new HashMap<>();
        for (UpdateCounterDto dto : dtos) {
            final var courseId = dto.getCourseId();
            final var operation = dto.getOperation();
            var currentCount = countDiffs.getOrDefault(courseId, 0);
            switch (operation) {
                case INCREMENT -> countDiffs.put(courseId, currentCount + 1);
                case DECREMENT -> countDiffs.put(courseId, currentCount - 1);
            }
        }
        for (Map.Entry<Long, Integer> entry : countDiffs.entrySet()) {
            final var courseId = entry.getKey();
            final var diff = entry.getValue();
            var course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new IllegalStateException("Course with id " + courseId + " not found"));
            var currentCount = course.getParticipantsCount();
            var newCount = currentCount + diff;
            if (newCount < 0) {
                throw new IllegalStateException("Course with id " + courseId + " has negative participant count");
            }
            course.setParticipantsCount(newCount);
            courseRepository.save(course);
        }
    }
}
