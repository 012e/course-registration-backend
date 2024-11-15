package com.u012e.session_auth_db.service;

import com.u012e.session_auth_db.dto.SubjectDto;
import com.u012e.session_auth_db.model.Subject;
import com.u012e.session_auth_db.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;

    @Override
    public long createSubject(SubjectDto subjectDto) {
        var subject = Subject.builder()
                .name(subjectDto.getName())
                .build();

        subjectRepository.save(subject);
        return subject.getId();
    }

    @Override
    public long updateSubject(long id, SubjectDto subjectDto) {
        var subject = subjectRepository.findById(id);

        if (subject.isEmpty()) {
            throw new IllegalArgumentException("Subject not found");
        }

        subject.get().setName(subjectDto.getName());

        subjectRepository.save(subject.get());
        return subject.get().getId();
    }

    @Override
    public void deleteSubject(long id) {
        var subject = subjectRepository.findById(id);

        if (subject.isEmpty()) {
            throw new IllegalArgumentException("Subject not found");
        }
        
        subjectRepository.deleteById(id);
    }

    @Override
    public SubjectDto getSubject(long id) {
        var subject = subjectRepository.findById(id);
        if (subject.isEmpty()) {
            throw new IllegalArgumentException("Subject not found");
        }

        return SubjectDto.builder().name(subject.get().getName()).build();
    }
}
