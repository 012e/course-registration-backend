package com.u012e.session_auth_db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Table(name = "results",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "course_id"})
        })
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Student student;

    @ManyToOne(optional = false)
    private Course course;

    @Column(nullable = false)
    private Boolean passed;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return Objects.equals(id, result.id) && Objects.equals(student, result.student) && Objects.equals(course, result.course) && Objects.equals(passed, result.passed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, student, course, passed);
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", student=" + student +
                ", course=" + course +
                ", passed=" + passed +
                '}';
    }
}
