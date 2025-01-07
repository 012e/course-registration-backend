package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.utils.RegistrationResult;

import java.util.List;

public interface CourseApplyRegistrationService {
    RegistrationResult register(Student student, List<Long> courseIds);
    RegistrationResult unregister(Student student, List<Long> courseIds);
}
