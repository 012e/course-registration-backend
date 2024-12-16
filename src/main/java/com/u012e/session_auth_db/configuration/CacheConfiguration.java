package com.u012e.session_auth_db.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("cache")
@EnableCaching
@Configuration
public class CacheConfiguration {
    public static final String PARTICIPANT_CACHE = "participantCache";
}
