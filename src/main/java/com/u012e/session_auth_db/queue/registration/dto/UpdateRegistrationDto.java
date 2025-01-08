package com.u012e.session_auth_db.queue.registration.dto;

import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRegistrationDto implements Serializable {
    @NotNull
    private Long studentId;
    @NotNull
    private Set<Long> courseIds;
    @NotNull
    private RegistrationOperation operation;
}