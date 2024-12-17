package com.u012e.session_auth_db.queue.counter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCounterDto implements Serializable {
    @NotNull
    private Long courseId;
    @NotNull
    private CounterOperation operation;
}