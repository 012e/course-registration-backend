package com.u012e.session_auth_db.controller;

import com.u012e.session_auth_db.dto.CreateCourseDto;
import com.u012e.session_auth_db.dto.ResponseCourseDto;
import com.u012e.session_auth_db.service.CourseService;
import com.u012e.session_auth_db.utils.GenericResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/course")
@Slf4j
public class CourseController {
    private final CourseService courseService;

    @PostMapping("/")
    public GenericResponse<Long> create(@Valid @RequestBody CreateCourseDto courseDto) {
        return GenericResponse.success(courseService.createCourse(courseDto));
    }

    @DeleteMapping("/")
    public GenericResponse<String> delete(@Parameter Long id) {
        courseService.deleteCourse(id);
        return GenericResponse.success();
    }

    @PutMapping("/")
    public GenericResponse<String> update(@RequestBody CreateCourseDto courseDto, @Parameter Long id) {
        courseService.updateCourse(id, courseDto);
        return GenericResponse.success();
    }

    @GetMapping("/{id}")
    public GenericResponse<ResponseCourseDto> getById(@PathVariable("id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        if (id < 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        return GenericResponse.success(courseService.getCourse(id));
    }

    @GetMapping("/")
    public GenericResponse<List<ResponseCourseDto>> get() {
        return GenericResponse.success(courseService.getAll());
    }
}
