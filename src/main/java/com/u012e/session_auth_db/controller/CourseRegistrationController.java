package com.u012e.session_auth_db.controller;

import com.u012e.session_auth_db.dto.CreateRegistrationDto;
import com.u012e.session_auth_db.dto.RegistrationResultDto;
import com.u012e.session_auth_db.dto.ResponseCourseDto;
import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.service.CachedCourseService;
import com.u012e.session_auth_db.service.CourseService;
import com.u012e.session_auth_db.service.StudentService;
import com.u012e.session_auth_db.service.registration.CourseRegistrationService;
import com.u012e.session_auth_db.utils.GenericResponse;
import com.u012e.session_auth_db.utils.RegistrationResult;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registration")
@Slf4j
public class CourseRegistrationController {
    private final CourseRegistrationService courseRegistrationService;
    private final CourseService courseService;
    private final StudentService studentService;

    @Autowired
    private CachedCourseService cachedCourseService;

    public CourseRegistrationController(CourseRegistrationService courseRegistrationService, CourseService courseService, StudentService studentService) {
        this.courseRegistrationService = courseRegistrationService;
        this.courseService = courseService;
        this.studentService = studentService;
    }

    @GetMapping("registered")
    public GenericResponse<List<ResponseCourseDto>> registeredSubjects(@AuthenticationPrincipal UserDetails userDetails) {
        var student = studentService.getStudentByUsername(userDetails.getUsername())
                .orElseThrow();
        if (cachedCourseService != null) {
            return GenericResponse.success(cachedCourseService.getAllRegisteredCourses(student.getId()));
        }
        return GenericResponse.success(courseService.getAllRegisteredCourse(student.getId()));
    }

    @PostMapping()
    public GenericResponse<RegistrationResultDto> register(@RequestBody @Valid CreateRegistrationDto registrationDto, @AuthenticationPrincipal UserDetails userDetails) {
        var username = userDetails.getUsername();
        var student = studentService.getStudentByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        log.trace("Student {} is registering for courses: {}", student, registrationDto.getCourseIds());
        var result = courseRegistrationService.register(student, registrationDto.getCourseIds());
        return mapResultDto(result);
    }

    @DeleteMapping()
    public GenericResponse<RegistrationResultDto> unregister(@RequestBody @Valid CreateRegistrationDto registrationDto, @AuthenticationPrincipal UserDetails userDetails) {
        var username = userDetails.getUsername();
        var student = studentService.getStudentByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        log.trace("Student {} is unregistering for courses: {}", student, registrationDto.getCourseIds());
        var result = courseRegistrationService.unregister(student, registrationDto.getCourseIds());
        return mapResultDto(result);
    }

    private GenericResponse<RegistrationResultDto> mapResultDto(RegistrationResult result) {
        var succeedIds = result.getSucceed()
                .stream()
                .map(Course::getId)
                .sorted()
                .toList();
        var failedIds = result.getFailed()
                .stream()
                .map(Course::getId)
                .sorted()
                .toList();
        var resultDto = RegistrationResultDto.builder()
                .succeed(succeedIds)
                .failed(failedIds)
                .build();
        return GenericResponse.success(resultDto);
    }
}
