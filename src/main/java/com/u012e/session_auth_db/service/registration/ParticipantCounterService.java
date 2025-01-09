package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Course;

import java.util.List;

public interface ParticipantCounterService {
    void takeSlot(Course course);

    void freeSlot(Course course);

    boolean isFull(Course course);

    int getCount(Course course);

    int getCount(Long courseId);

    List<Integer> getCounts(List<Long> courseIds);
}
