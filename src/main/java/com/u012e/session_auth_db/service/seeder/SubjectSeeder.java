package com.u012e.session_auth_db.service.seeder;


import com.u012e.session_auth_db.model.Subject;
import com.u012e.session_auth_db.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectSeeder implements Seeder<Subject> {
    private final SubjectRepository subjectRepository;
    private final Faker faker;
    private final List<Subject> subjectList;

    @Override
    public void seed(int count) {
        for (int i = 0; i < count; ++i) {
            Subject subject = new Subject();
            subject.setName(faker.educator().course());
            subjectList.add(subject);
        }
        subjectRepository.saveAll(subjectList);
    }
}
