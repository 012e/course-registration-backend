package com.u012e.session_auth_db.controller;

import com.u012e.session_auth_db.dto.CreateCourseDto;
import com.u012e.session_auth_db.dto.ResponseCourseDto;
import com.u012e.session_auth_db.service.CachedCourseService;
import com.u012e.session_auth_db.service.CourseService;
import com.u012e.session_auth_db.utils.GenericResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/course")
@Slf4j
public class CourseController {
    private final CourseService courseService;

    @Autowired(required = false)
    private CachedCourseService cachedCourseService;

    public CourseController(CourseService courseService, CachedCourseService cachedCourseService) {
        this.courseService = courseService;
        this.cachedCourseService = cachedCourseService;
    }

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
    public GenericResponse<List<ResponseCourseDto>> getAll() {
        if (cachedCourseService != null) {
            return GenericResponse.success(cachedCourseService.getAllCourses());
        }
        return GenericResponse.success(courseService.getAll());
    }

}
