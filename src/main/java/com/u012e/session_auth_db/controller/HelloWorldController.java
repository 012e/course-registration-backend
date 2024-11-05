package com.u012e.session_auth_db.controller;

import com.u012e.session_auth_db.utils.GenericResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloWorldController {
    @GetMapping("/secret")
    public GenericResponse<Map<String, String>> hello() {
        return GenericResponse.<Map<String, String>>builder()
                .message("Secret data")
                .data(null)
                .success(true)
                .build();
    }

    @GetMapping("/public/hello")
    public GenericResponse<Map<String, String>> helloPublic() {
        return GenericResponse.<Map<String, String>>builder()
                .message("Hello, World!")
                .data(null)
                .success(true)
                .build();
    }
}
