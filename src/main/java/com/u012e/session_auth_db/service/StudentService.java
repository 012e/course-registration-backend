package com.u012e.session_auth_db.service;

import com.u012e.session_auth_db.model.Student;

import java.util.Optional;

public interface StudentService {
    Optional<Student> getStudentById(Long studentId);

    Optional<Student> getStudentByUsername(String username);
}
