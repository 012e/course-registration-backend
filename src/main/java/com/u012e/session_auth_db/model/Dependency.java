package com.u012e.session_auth_db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "dependencies", uniqueConstraints = {
        @UniqueConstraint(name = "subject_required_subject_unique", columnNames = {"subject_id", "required_subject_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Dependency implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "required_subject_id", nullable = false)
    private Long requiredSubjectId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return Objects.equals(id, that.id) && Objects.equals(subjectId, that.subjectId) && Objects.equals(requiredSubjectId, that.requiredSubjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subjectId, requiredSubjectId);
    }

    @Override
    public String toString() {
        return "Dependency{" +
                "id=" + id +
                ", subjectId=" + subjectId +
                ", requiredSubjectId=" + requiredSubjectId +
                '}';
    }
}
