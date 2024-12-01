package com.u012e.session_auth_db.repository;

import com.u012e.session_auth_db.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    public List<Course> findTop10ByOrderByIdAsc();
}
