package com.u012e.session_auth_db.configuration;

import com.u012e.session_auth_db.utils.RequestAndResponseLoggingFilter;
import net.datafaker.Faker;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class ProjectConfiguration {
    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    Faker faker() {
        return new Faker();
    }

    @Bean
    Random random() {
        return new Random();
    }

    @Bean
    public RequestAndResponseLoggingFilter requestResponseLoggingFilter() {
        return new RequestAndResponseLoggingFilter();
    }
}
