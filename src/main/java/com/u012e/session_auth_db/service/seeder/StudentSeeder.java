package com.u012e.session_auth_db.service.seeder;


import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class StudentSeeder implements Seeder<Student> {
    private static final String DEFAULT_PASSWORD = "password";
    private final StudentRepository studentRepository;
    private final Faker faker;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void seed(int count) {
        var students = new ArrayList<Student>();
        for (int i = 0; i < count; ++i) {
            var student = Student
                    .builder()
                    .firstName(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    .username(faker.internet().username())
                    .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                    .build();
            students.add(student);
        }
        studentRepository.saveAll(students);
    }
}
