package com.u012e.session_auth_db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
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

    @ManyToOne(optional = false)
    private Subject subject;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
}
