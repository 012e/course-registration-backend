package com.u012e.session_auth_db.queue.counter;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.queue.counter.dto.CounterOperation;
import com.u012e.session_auth_db.queue.counter.dto.UpdateCounterDto;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CounterProducer {
    private final Queue queue;
    private final RabbitTemplate rabbitTemplate;

    public CounterProducer(@Qualifier("counter") Queue queue, RabbitTemplate rabbitTemplate) {
        this.queue = queue;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void increase(Course course) {
        final var id = course.getId();
        var dto = UpdateCounterDto.builder()
                .courseId(id)
                .operation(CounterOperation.INCREMENT)
                .build();
        rabbitTemplate.convertAndSend(queue.getName(), dto);
    }

    public void decrease(Course course) {
        final var id = course.getId();
        var dto = UpdateCounterDto.builder()
                .courseId(id)
                .operation(CounterOperation.DECREMENT)
                .build();
        rabbitTemplate.convertAndSend(queue.getName(), dto);
    }
}
