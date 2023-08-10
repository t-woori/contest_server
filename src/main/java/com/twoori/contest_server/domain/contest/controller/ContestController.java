package com.twoori.contest_server.domain.contest.controller;

import com.twoori.contest_server.domain.contest.dto.ContestDTO;
import com.twoori.contest_server.domain.contest.service.ContestService;
import com.twoori.contest_server.domain.contest.vo.EnterContestVO;
import com.twoori.contest_server.global.exception.PermissionDenialException;
import com.twoori.contest_server.global.security.StudentJwtProvider;
import com.twoori.contest_server.global.util.Utils;
import com.twoori.contest_server.global.vo.CommonMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
public class ContestController {

    private final StudentJwtProvider studentJwtProvider;
    private final Utils utils;
    private final ContestService contestService;

    public ContestController(StudentJwtProvider studentJwtProvider, Utils utils, ContestService contestService) {
        this.studentJwtProvider = studentJwtProvider;
        this.utils = utils;
        this.contestService = contestService;
    }

    @GetMapping("/v1/contest/{contest_id}/enter")
    public ResponseEntity<EnterContestVO> requestEnterContest(
            @RequestHeader(name = "Authorization") String accessTokenHeader,
            @PathVariable("contest_id") UUID contestId) {
        log.debug("access token: {}, contest_id: {}", accessTokenHeader, contestId);
        String accessToken = utils.parseAccessTokenAboutAuthorizationHeader(accessTokenHeader);
        if (!studentJwtProvider.validateAccessToken(accessToken)) {
            throw new PermissionDenialException("Not Found Access Token");
        }
        UUID studentId = studentJwtProvider.getStudentIdByAccessToken(accessToken);
        LocalDateTime now = LocalDateTime.now();
        ContestDTO dto = contestService.getAccessibleContest(studentId, contestId, now);
        return ResponseEntity.ok(
                new EnterContestVO(
                        CommonMessage.OK.getMessage(),
                        dto.runningStartDateTime(),
                        dto.runningEndDateTime()
                )
        );
    }

}
