package com.u012e.session_auth_db.service;

import com.u012e.session_auth_db.dto.CreateCourseDto;
import com.u012e.session_auth_db.dto.ResponseCourseDto;
import com.u012e.session_auth_db.dto.ResponseSubjectDto;
import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.repository.CourseRepository;
import com.u012e.session_auth_db.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public long createCourse(CreateCourseDto courseDto) {
        var subject = subjectRepository.findById(courseDto.getSubjectId());

        if (subject.isEmpty()) {
            throw new IllegalArgumentException("Subject not found");
        }

        if (courseDto.getStartPeriod() >= courseDto.getEndPeriod()) {
            throw new IllegalArgumentException("Start period must be less than end period");
        }

        var course = Course.builder()
                .dayOfWeek(courseDto.getDayOfWeek())
                .maxParticipants(courseDto.getMaxParticipants())
                .startPeriod(courseDto.getStartPeriod())
                .endPeriod(courseDto.getEndPeriod())
                .subject(subject.get())
                .build();

        return courseRepository.save(course).getId();
    }

    @Override
    public ResponseCourseDto getCourse(long id) {
        var course = courseRepository.findById(id);

        if (course.isEmpty()) {
            throw new IllegalArgumentException("Course not found");
        }

        return ResponseCourseDto.builder()
                .dayOfWeek(course.get().getDayOfWeek())
                .maxParticipants(course.get().getMaxParticipants())
                .startPeriod(course.get().getStartPeriod())
                .endPeriod(course.get().getEndPeriod())
                .subject(
                        ResponseSubjectDto.builder()
                                .name(course.get().getSubject().getName())
                                .id(course.get().getSubject().getId())
                                .build())
                .build();
    }

    @Override
    public void deleteCourse(long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public void updateCourse(long id, CreateCourseDto courseDto) {
        var course = courseRepository.findById(id);

        if (course.isEmpty()) {
            throw new IllegalArgumentException("Course not found");
        }

        var subject = subjectRepository.findById(courseDto.getSubjectId());
        if (subject.isEmpty()) {
            throw new IllegalArgumentException("Subject not found");
        }
        course.get().setSubject(subject.get());

        if (courseDto.getMaxParticipants() > 0) {
            course.get().setMaxParticipants(courseDto.getMaxParticipants());
        } else {
            throw new IllegalArgumentException("Max participants must be greater than zero");
        }

        if (courseDto.getStartPeriod() > 0) {
            course.get().setStartPeriod(courseDto.getStartPeriod());
        } else {
            throw new IllegalArgumentException("Start period must be greater than zero");
        }

        if (courseDto.getEndPeriod() > courseDto.getStartPeriod()) {
            course.get().setEndPeriod(courseDto.getEndPeriod());
        } else {
            throw new IllegalArgumentException("End period must be less than start period");
        }

        if (courseDto.getDayOfWeek() > 0) {
            course.get().setDayOfWeek(courseDto.getDayOfWeek());
        } else {
            throw new IllegalArgumentException("DayOfWeek must be greater than zero");
        }

        courseRepository.save(course.get());
    }
}
