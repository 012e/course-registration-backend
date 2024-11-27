package com.u012e.session_auth_db.controller;

import com.u012e.session_auth_db.dto.CreateRegistrationDto;
import com.u012e.session_auth_db.service.StudentService;
import com.u012e.session_auth_db.service.registration.CourseRegistrationService;
import com.u012e.session_auth_db.utils.GenericResponse;
import com.u012e.session_auth_db.utils.RegistrationResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
@Slf4j
public class CourseRegistrationController {
    private final CourseRegistrationService courseRegistrationService;
    private final StudentService studentService;

    @PostMapping()
    public GenericResponse<RegistrationResult> register(@RequestBody @Valid CreateRegistrationDto registrationDto, @AuthenticationPrincipal UserDetails userDetails) {
        var username = userDetails.getUsername();
        var student = studentService.getStudentByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        log.trace("Student {} is registering for courses: {}", student, registrationDto.getCourseIds());
        var result = courseRegistrationService.register(student, registrationDto.getCourseIds());
        return GenericResponse.success(result);
    }
}
