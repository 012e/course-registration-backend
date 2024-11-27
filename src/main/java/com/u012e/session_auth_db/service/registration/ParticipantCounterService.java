package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Course;

public interface ParticipantCounterService {
    void takeSlot(Course course);

    void giveUpSlot(Course course);

    boolean isFull(Course course);
}
