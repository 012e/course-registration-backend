package com.u012e.session_auth_db.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResultDto {
    private List<Long> succeed = new ArrayList<>();
    private List<Long> failed = new ArrayList<>();
}
