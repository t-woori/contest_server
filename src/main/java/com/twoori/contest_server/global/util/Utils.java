package com.twoori.contest_server.global.util;

import com.twoori.contest_server.global.exception.PermissionDenialException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Utils {
    public String parseAccessTokenAboutAuthorizationHeader(String lineOfAuthorizationHeader) {
        if (!lineOfAuthorizationHeader.startsWith("Bearer")) {
            throw new PermissionDenialException("not found access token");
        }
        return Objects.requireNonNull(lineOfAuthorizationHeader.substring(7).trim());
    }
}
