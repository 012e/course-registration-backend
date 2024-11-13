package com.u012e.session_auth_db.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class DependencyDto {
    @NotNull
    @Positive
    private Long id;

    @NotNull
    private List<@Positive Long> dependencyIds;
}
