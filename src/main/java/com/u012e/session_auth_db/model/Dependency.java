package com.u012e.session_auth_db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dependencies", uniqueConstraints = {
        @UniqueConstraint(name = "subject_required_subject_unique", columnNames = {"subject_id", "required_subject_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Dependency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(optional = false)
    @JoinColumn(name = "required_subject_id", nullable = false)
    private Subject requiredSubject;

}
