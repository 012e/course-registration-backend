package com.u012e.session_auth_db.queue.counter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCounterDto {
    private Long courseId;
    private Long count;
}