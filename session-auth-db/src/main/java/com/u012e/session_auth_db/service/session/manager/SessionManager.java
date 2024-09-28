package com.u012e.session_auth_db.service.session.manager;

import com.u012e.session_auth_db.dto.SessionDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface SessionManager {
    SessionDto createSession(String username);

    Optional<SessionDto> getSession(String token);

    void invalidateSession(HttpServletRequest request);
}
