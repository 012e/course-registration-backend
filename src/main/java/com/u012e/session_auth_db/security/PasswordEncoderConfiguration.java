package com.u012e.session_auth_db.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordEncoderConfiguration {
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
