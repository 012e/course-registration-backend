package com.u012e.session_auth_db.service;

import com.u012e.session_auth_db.dto.SubjectDto;

public interface SubjectService {
    long createSubject(SubjectDto subject);
    void deleteSubject(long id);
    long updateSubject(long id, SubjectDto subject);
    SubjectDto getSubject(long id);
}
