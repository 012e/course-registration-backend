package com.u012e.session_auth_db.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCourseDto {
    private Integer startPeriod;
    private Integer endPeriod;
    private Integer dayOfWeek;
    private Integer maxParticipants;
    private ResponseSubjectDto subject;
}
