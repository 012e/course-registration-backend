package com.u012e.session_auth_db.service;

import com.u012e.session_auth_db.dto.CreateCourseDto;
import com.u012e.session_auth_db.dto.ResponseCourseDto;
import com.u012e.session_auth_db.model.Course;

import java.util.List;

public interface CourseService {
    long createCourse(CreateCourseDto courseDto);

    ResponseCourseDto getCourse(long id);

    void deleteCourse(long id);

    void updateCourse(long id, CreateCourseDto courseDto);

    List<Course> getAllById(List<Long> ids);

    List<ResponseCourseDto> getAll();
}
