package com.u012e.session_auth_db.controller;

import com.u012e.session_auth_db.dto.CreateSubjectDto;
import com.u012e.session_auth_db.dto.ResponseSubjectDto;
import com.u012e.session_auth_db.service.SubjectService;
import com.u012e.session_auth_db.utils.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/subject")
@Slf4j
public class SubjectController {
    private final SubjectService subjectService;

    @Operation(method = "POST", description = "Create a subject")
    @PostMapping("/")
    public GenericResponse<Long> create(@Valid @RequestBody CreateSubjectDto subjectDto) {
        var id = subjectService.createSubject(subjectDto);
        return GenericResponse.success(id);
    }

    @Operation(method = "DELETE", description = "Delete a subject")
    @DeleteMapping("/")
    public GenericResponse<Long> delete(@Parameter long id) {
        subjectService.deleteSubject(id);
        return GenericResponse.success();
    }

    @Operation(method = "PUT", description = "Update a subject")
    @PutMapping("/")
    public GenericResponse<Long> update(@Parameter long id, @Valid @RequestBody CreateSubjectDto subjectDto) {
        var returnedId = subjectService.updateSubject(id, subjectDto);
        return GenericResponse.success(returnedId);
    }

    @Operation(method = "GET", description = "Get a subject")
    @GetMapping("/")
    public GenericResponse<ResponseSubjectDto> get(@Parameter long id) {
        var returnSubject = subjectService.getSubject(id);
        return GenericResponse.success(returnSubject);
    }
}
