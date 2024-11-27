package com.u012e.session_auth_db.utils;

import com.u012e.session_auth_db.model.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResult {
    private Set<Course> succeed = new HashSet<>();
    private Set<Course> failed = new HashSet<>();
}
