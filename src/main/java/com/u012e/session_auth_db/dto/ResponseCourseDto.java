package com.u012e.session_auth_db.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCourseDto implements Serializable {
    private Long id;
    private Integer startPeriod;
    private Integer endPeriod;
    private Integer dayOfWeek;
    private Integer maxParticipants;
    private Integer participantsCount;
    private ResponseSubjectDto subject;
}
