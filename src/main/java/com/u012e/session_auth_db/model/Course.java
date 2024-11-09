package com.u012e.session_auth_db.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @ManyToOne(optional = false)
    private Subject subject;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
}
