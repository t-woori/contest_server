package com.twoori.contest_server.domain.problem.repository;

public record ContentDto(Long contentId, String answer, String preScript, String question, String postScript,
                         String hint) {
}
