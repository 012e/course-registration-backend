package com.u012e.session_auth_db.repository;

import com.u012e.session_auth_db.model.Dependency;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface DependencyRepository extends JpaRepository<Dependency, Long> {
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO dependencies (subject_id, required_subject_id) VALUES (:subjectId, :requiredSubjectId)", nativeQuery = true)
    void addByIds(Long subjectId, Long requiredSubjectId);

    @Query(value = "SELECT * FROM dependencies WHERE subject_id = :subjectId", nativeQuery = true)
    Set<Dependency> findAllBySubjectId(@Param("subjectId") Long subjectId);

    @Transactional
    default void addAllByIds(Iterable<Pair<Long, Long>> pairs) {
        for (Pair<Long, Long> pair : pairs) {
            addByIds(pair.getFirst(), pair.getSecond());
        }
    }
}

