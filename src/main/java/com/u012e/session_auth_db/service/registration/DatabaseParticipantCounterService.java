package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatabaseParticipantCounterService implements ParticipantCounterService {
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public void takeSlot(Course course) {
        var count = course.getParticipantsCount();
        var max = course.getMaxParticipants();
        if (count >= max) {
            throw new IllegalStateException("Trying to take slot on course that is already full");
        }
        course.setParticipantsCount(count + 1);
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public void giveUpSlot(Course course) {
        var count = course.getParticipantsCount();
        if (count == 0) {
            throw new IllegalStateException("Trying to give up slot on empty course");
        }
        course.setParticipantsCount(count - 1);
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public boolean isFull(Course course) {
        return course.getParticipantsCount() >= course.getMaxParticipants();
    }
}
