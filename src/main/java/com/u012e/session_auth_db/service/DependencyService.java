package com.u012e.session_auth_db.service;

import com.u012e.session_auth_db.dto.DependencyDto;
import com.u012e.session_auth_db.model.Dependency;
import com.u012e.session_auth_db.model.Subject;
import com.u012e.session_auth_db.repository.DependencyRepository;
import com.u012e.session_auth_db.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DependencyService {
    private final DependencyRepository dependencyRepository;
    private final SubjectRepository subjectRepository;

    public void createDependency(DependencyDto dependencyDto) {
        Subject subject;
        List<Subject> requiredSubjects;

        try {
            subject = subjectRepository.findById(dependencyDto.getId()).orElseThrow();
            requiredSubjects = subjectRepository.findAllById(dependencyDto.getDependencyIds());
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Subject or required subject not found");
        }

        var dependencies = new ArrayList<Dependency>(requiredSubjects.size());
        for (var required : requiredSubjects) {
            var dependency = new Dependency();
            dependency.setSubjectId(subject.getId());
            dependency.setRequiredSubjectId(required.getId());
            dependencies.add(dependency);
        }
        try {
            dependencyRepository.saveAll(dependencies);
        } catch (Exception e) {
            throw new IllegalArgumentException("Some of your dependencies are already existed");
        }
    }

    public void patchDependency(DependencyDto dependencyDto) {
        Subject subject;
        List<Subject> requiredSubjects;

        try {
            subject = subjectRepository.findById(dependencyDto.getId()).orElseThrow();
            requiredSubjects = subjectRepository.findAllById(dependencyDto.getDependencyIds());
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Subject or required subject not found");
        }

        var dependencies = new ArrayList<Dependency>(requiredSubjects.size());
        for (var required : requiredSubjects) {
            var dependency = new Dependency();
            dependency.setSubjectId(subject.getId());
            dependency.setRequiredSubjectId(required.getId());
            dependencies.add(dependency);
        }
        try {
            dependencyRepository.saveAll(dependencies);
        } catch (DataIntegrityViolationException ignore) {
            // TODO: just a workaround, it just ignores all exceptions
        }
    }

    public Set<Subject> getDependencies(Subject subject) {
        return dependencyRepository
                .findAllBySubjectId(subject.getId())
                .stream()
                .map(dep -> subjectRepository
                        .findById(dep.getRequiredSubjectId())
                        .orElseThrow(() -> new IllegalArgumentException("Subject not found"))
                )
                .collect(Collectors.toSet());
    }
}
