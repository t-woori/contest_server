package com.twoori.contest_server.domain.contest.controller;

import com.twoori.contest_server.domain.contest.service.ContestService;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
public class ContestController {

    private final ContestService contestService;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }

    @PostMapping("/v1/contest/{contest_id}/enter")
    public HttpEntity<Boolean> requestEnterContest(@PathVariable("contest_id") UUID contestId) {
        LocalDateTime now = LocalDateTime.now();
        return new HttpEntity<>(contestService.isEnterContest(contestId, now));
    }
}
