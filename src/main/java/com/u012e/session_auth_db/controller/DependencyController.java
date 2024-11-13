package com.u012e.session_auth_db.controller;

import com.u012e.session_auth_db.dto.DependencyDto;
import com.u012e.session_auth_db.service.DependencyService;
import com.u012e.session_auth_db.utils.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dependency")
@RequiredArgsConstructor
public class DependencyController {
    private final DependencyService dependencyService;

    @PostMapping("/")
    public ResponseEntity<GenericResponse<Object>> createDependency(@RequestBody @Valid DependencyDto dependencyDto) {
        dependencyService.createDependency(dependencyDto);
        return ResponseEntity.created(null).body(
                GenericResponse.builder()
                        .message("Dependency created")
                        .data(null)
                        .success(true)
                        .build());
    }

    @PatchMapping("/")
    public ResponseEntity<GenericResponse<Object>> patchDependency(@RequestBody @Valid DependencyDto dependencyDto) {
        dependencyService.patchDependency(dependencyDto);
        return ResponseEntity.ok(GenericResponse.builder()
                .message("Dependency patched")
                .data(null)
                .success(true)
                .build());
    }
}
