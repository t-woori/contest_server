package com.twoori.contest_server.domain.contest.controller;

import com.twoori.contest_server.domain.contest.dto.ContestDto;
import com.twoori.contest_server.domain.contest.service.ContestService;
import com.twoori.contest_server.domain.contest.vo.EnterContestVOAPI;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.controller.SecurityController;
import com.twoori.contest_server.global.security.StudentJwtProvider;
import com.twoori.contest_server.global.util.Utils;
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
public class ContestController extends SecurityController {


    private final ContestService contestService;

    public ContestController(StudentJwtProvider studentJwtProvider, Utils utils, ContestService contestService) {
        super(utils, studentJwtProvider);
        this.contestService = contestService;
    }

    @GetMapping("/v1/contest/{contest_id}/enter")
    public ResponseEntity<EnterContestVOAPI> requestEnterContest(
            @RequestHeader(name = "Authorization") String accessTokenHeader,
            @PathVariable("contest_id") UUID contestId) {
        StudentDto studentDto = super.validateAuthorization(accessTokenHeader);
        LocalDateTime now = LocalDateTime.now();
        ContestDto dto = contestService.getAccessibleContest(studentDto.id(), contestId, now);
        return ResponseEntity.ok(
                new EnterContestVOAPI(
                        dto.runningStartDateTime(),
                        dto.runningEndDateTime()
                )
        );
    }

}
