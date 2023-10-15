package com.twoori.contest_server.domain.student.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ContestStatus(UUID contestID, @JsonProperty("status") ProblemStatus problemStatus) {
}
