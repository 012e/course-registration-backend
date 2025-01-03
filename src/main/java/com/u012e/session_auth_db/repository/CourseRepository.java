package com.u012e.session_auth_db.repository;

import com.u012e.session_auth_db.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findTop10ByOrderByIdAsc();

    Stream<Course> findAllBy();
}
