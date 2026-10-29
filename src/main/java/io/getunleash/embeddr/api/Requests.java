package io.getunleash.embeddr.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Shared request checks. Every endpoint needs the browser's session id. */
final class Requests {

    static String requireSessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sessionId is required");
        }
        return sessionId;
    }

    private Requests() {}
}
