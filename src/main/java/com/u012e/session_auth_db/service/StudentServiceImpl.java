package com.u012e.session_auth_db.service;

import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    @Override
    public Optional<Student> getStudentById(Long studentId) {
        return studentRepository.findById(studentId);
    }

    @Override
    public Optional<Student> getStudentByUsername(String username) {
        return studentRepository.findByUsername(username);
    }
}
