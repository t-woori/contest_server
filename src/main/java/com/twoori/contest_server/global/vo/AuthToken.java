package com.twoori.contest_server.global.vo;

public record AuthToken(
        String accessToken,
        String refreshToken
) {
}
