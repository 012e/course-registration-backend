package com.u012e.session_auth_db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    private Student student;

    @ManyToOne(optional = false)
    private Course course;

    @Column(nullable = false)
    private Boolean passed;

}
