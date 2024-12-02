package com.u012e.session_auth_db.configuration;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
@RequiredArgsConstructor
public class DataFakerConfiguration {
    private final Random random;

    @Bean
    public Faker faker() {
        return new Faker(random);
    }
}
