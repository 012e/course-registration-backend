package com.u012e.session_auth_db.service;

import com.u012e.session_auth_db.dto.CreateCourseDto;
import com.u012e.session_auth_db.dto.ResponseCourseDto;
import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.repository.CourseRepository;
import com.u012e.session_auth_db.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;

    @Override
    public long createCourse(CreateCourseDto courseDto) {
        var subject = subjectRepository.findById(courseDto.getSubjectId());

        if (subject.isEmpty()) {
            throw new IllegalArgumentException("Subject not found");
        }

        if (courseDto.getStartPeriod() >= courseDto.getEndPeriod()) {
            throw new IllegalArgumentException("Start period must be less than end period");
        }

        var course = modelMapper.map(courseDto, Course.class);

        return courseRepository.save(course)
                .getId();
    }

    @Override
    public ResponseCourseDto getCourse(long id) {
        var course = courseRepository.findById(id);

        if (course.isEmpty()) {
            throw new IllegalArgumentException("Course not found");
        }

        return modelMapper.map(course.get(), ResponseCourseDto.class);
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
        course.get()
                .setSubject(subject.get());

        if (courseDto.getMaxParticipants() > 0) {
            course.get()
                    .setMaxParticipants(courseDto.getMaxParticipants());
        } else {
            throw new IllegalArgumentException("Max participants must be greater than zero");
        }

        if (courseDto.getStartPeriod() > 0) {
            course.get()
                    .setStartPeriod(courseDto.getStartPeriod());
        } else {
            throw new IllegalArgumentException("Start period must be greater than zero");
        }

        if (courseDto.getEndPeriod() > courseDto.getStartPeriod()) {
            course.get()
                    .setEndPeriod(courseDto.getEndPeriod());
        } else {
            throw new IllegalArgumentException("End period must be less than start period");
        }

        if (courseDto.getDayOfWeek() > 0) {
            course.get()
                    .setDayOfWeek(courseDto.getDayOfWeek());
        } else {
            throw new IllegalArgumentException("DayOfWeek must be greater than zero");
        }

        courseRepository.save(course.get());
    }

    @Override
    public List<Course> getAllById(List<Long> ids) {
        var courses = courseRepository.findAllById(ids);
        if (courses.size() != ids.size()) {
            throw new IllegalArgumentException("Some courses not found");
        }
        return courses;
    }

    @Override
    public List<ResponseCourseDto> getAll() {
        return courseRepository
                .findAll()
                .stream()
                .map(course -> modelMapper.map(course, ResponseCourseDto.class))
                .toList();
    }

    @Override
    public List<Course> getCourseByIds(List<Long> ids) {
        return courseRepository.findAllById(ids);
    }
}
