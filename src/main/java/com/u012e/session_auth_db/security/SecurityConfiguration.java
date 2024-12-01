package com.u012e.session_auth_db.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final AuthenticationManager authenticationManager;

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    private static String[] whiteList = {
            "/cache/**",
            "/auth/forceLogin",
            "/auth/login",
            "/auth/register",
            "/auth/logout",
            "/schema",
            "/schema/**",
            "/public/**",
            "/seed/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

        //csrf & cors
        http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults());

        // filter
        http.authorizeHttpRequests(
                (request) -> {
                    request.requestMatchers(whiteList).permitAll();
                    request.requestMatchers("/**").fullyAuthenticated();
                }
        );

        //storing the session
        http.securityContext((context) -> context.securityContextRepository(securityContextRepository));

        //session management
        http.sessionManagement((session) -> {
                    session.maximumSessions(1).maxSessionsPreventsLogin(true);
                    session.sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession);
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                }
        );

        // clear cookie when logout
        http.logout((logout) -> {
                    logout.logoutUrl("/auth/logout");
                    logout.addLogoutHandler(
                            new HeaderWriterLogoutHandler(
                                    new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.COOKIES)
                            )
                    );
                    logout.deleteCookies("SESSION");
                }
        );

        //auth provider for connect DAO
        http.authenticationManager(authenticationManager);

        return http.build();
    }
}
