package com.u012e.session_auth_db.service;

import com.u012e.session_auth_db.dto.CreateSubjectDto;
import com.u012e.session_auth_db.dto.ResponseSubjectDto;

public interface SubjectService {
    long createSubject(CreateSubjectDto subjectDto);
    void deleteSubject(long id);
    long updateSubject(long id, CreateSubjectDto subjectDto);
    ResponseSubjectDto getSubject(long id);
}
