package com.u012e.session_auth_db.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubjectDto {
    @NonNull
    private String name;
}

