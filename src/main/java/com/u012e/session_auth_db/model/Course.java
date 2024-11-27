package com.u012e.session_auth_db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private int startPeriod;

    @Column(nullable = false)
    private int endPeriod;

    @Column(nullable = false)
    private int dayOfWeek;

    @Column(nullable = false)
    private int maxParticipants;

    @Column(nullable = false)
    private int participantsCount;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return startPeriod == course.startPeriod && endPeriod == course.endPeriod && dayOfWeek == course.dayOfWeek && maxParticipants == course.maxParticipants && participantsCount == course.participantsCount && Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startPeriod, endPeriod, dayOfWeek, maxParticipants, participantsCount);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", participantsCount=" + participantsCount +
                ", maxParticipants=" + maxParticipants +
                ", dayOfWeek=" + dayOfWeek +
                ", endPeriod=" + endPeriod +
                ", startPeriod=" + startPeriod +
                '}';
    }

    @ManyToOne(optional = false)
    private Subject subject;


    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
}
