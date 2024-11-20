package com.u012e.session_auth_db.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseDto {
    @NonNull
    private Integer startPeriod;
    @NotNull
    private Integer endPeriod;
    @NotNull
    private Integer dayOfWeek;
    @NotNull
    private Integer maxParticipants;
    @NotNull
    private long subjectId;
}
