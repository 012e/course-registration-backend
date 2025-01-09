package com.u012e.session_auth_db.service;

import com.u012e.session_auth_db.configuration.CacheConfiguration;
import com.u012e.session_auth_db.dto.ResponseCourseDto;
import com.u012e.session_auth_db.service.registration.ParticipantCounterService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class CachedCourseService {
    private final ValueOperations<String, HashSet<Long>> registeredCourseCache;
    private final ValueOperations<String, ArrayList<ResponseCourseDto>> courseCache;
    private final ParticipantCounterService participantCounterService;
    private final CourseService courseService;

    public CachedCourseService(ValueOperations<String, HashSet<Long>> registeredCourseCache,
                               ValueOperations<String, ArrayList<ResponseCourseDto>> courseCache,
                               ParticipantCounterService participantCounterService,
                               ModelMapper modelMapper,
                               CourseService courseService) {
        this.registeredCourseCache = registeredCourseCache;
        this.courseCache = courseCache;
        this.participantCounterService = participantCounterService;
        this.courseService = courseService;
    }

    private String getKeyOfCourses() {
        return String.format("%s", CacheConfiguration.ALL_COURSES);
    }

    public List<ResponseCourseDto> getAllCourses() {
        var courses = getStaleCourses();
        syncCounter(courses);
        return courses;
    }

    private List<ResponseCourseDto> getStaleCourses() {
        final var key = getKeyOfCourses();
        var courses = courseCache.get(key);
        if (courses == null) {
            return new ArrayList<>();
        }
        return courses;
    }

    private void syncCounter(List<ResponseCourseDto> courses) {
        var courseCounts = participantCounterService.getCounts(courses.parallelStream()
                .map(ResponseCourseDto::getId)
                .toList());
        for (int i = 0; i < courses.size(); i++) {
            courses.get(i)
                    .setParticipantsCount(courseCounts.get(i));
        }
    }

    private String getKeyOfRegistration(Long studentId) {
        return String.format("%s:%d", CacheConfiguration.REGISTRATION_CACHE, studentId);
    }


    public List<ResponseCourseDto> getAllRegisteredCourses(Long studentId) {
        final var key = getOfRegistration(studentId);
        registeredCourseCache.setIfAbsent(key, new HashSet<>());
        var courseIdsSet = registeredCourseCache.get(key);
        if (courseIdsSet == null) {
            throw new IllegalStateException("Course can't be null");
        }
        var courseIds = new ArrayList<>(courseIdsSet);
        Collections.sort(courseIds);
        var allCourses = getStaleCourses();
        var result = new ArrayList<ResponseCourseDto>(courseIdsSet.size());

        int i = 0, j = 0;
        while (i < courseIds.size() && j < allCourses.size()) {
            if (Objects.equals(courseIds.get(i), allCourses.get(j)
                    .getId())) {
                result.add(getStaleCourses().get(j));
                i++;
                j++;
            } else if (courseIds.get(i) < allCourses.get(j)
                    .getId()) {
                i++;
            } else {
                j++;
            }
        }

        syncCounter(result);
        return result;
    }

    private String getOfRegistration(Long studentId) {
        return getKeyOfRegistration(studentId);
    }

    public void syncCache() {
        var courses = courseService.getAll();
        courses.sort(Comparator.comparing(ResponseCourseDto::getId));
        var key = getKeyOfCourses();
        courseCache.set(key, new ArrayList<>(courses));
    }
}
