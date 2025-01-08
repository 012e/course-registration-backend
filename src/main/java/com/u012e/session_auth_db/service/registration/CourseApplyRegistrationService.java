package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;

import java.util.HashSet;
import java.util.Set;

public interface CourseApplyRegistrationService {
    void applyRegistration(Student student, Set<Course> courses);
    void removeRegistration(Student student, Set<Course> courses);
}
