package com.twoori.contest_server.domain.contest.controller;

import com.twoori.contest_server.domain.contest.dto.EnterContestDtoForController;
import com.twoori.contest_server.domain.contest.dto.RegisteredContestDto;
import com.twoori.contest_server.domain.contest.dto.SearchContestDtoForController;
import com.twoori.contest_server.domain.contest.mapper.ContestControllerVOMapper;
import com.twoori.contest_server.domain.contest.service.ContestService;
import com.twoori.contest_server.domain.contest.vo.*;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.security.SecurityUtil;
import com.twoori.contest_server.global.vo.CommonAPIResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
public class ContestController {

    private final ContestService contestService;
    private final SecurityUtil securityUtil;
    private final ContestControllerVOMapper mapper;

    public ContestController(ContestService contestService, SecurityUtil securityUtil, ContestControllerVOMapper mapper) {
        this.contestService = contestService;
        this.securityUtil = securityUtil;
        this.mapper = mapper;
    }

    @GetMapping("/v1/contest/{contest_id}/enter")
    public ResponseEntity<EnterContestVOAPI> requestEnterContest(
            @RequestHeader(name = "Authorization") String accessTokenHeader,
            @PathVariable("contest_id") UUID contestId) {
        StudentDto studentDto = securityUtil.validateAuthorization(accessTokenHeader);
        LocalDateTime now = LocalDateTime.now();
        EnterContestDtoForController result = contestService.enterStudentInContest(studentDto.id(), contestId, now);
        return ResponseEntity.ok(
                new EnterContestVOAPI(
                        result.runningStartDateTime(),
                        result.runningEndDateTime()
                )
        );
    }

    @GetMapping("/v1/contest")
    public ResponseEntity<SearchContestsVO> searchContests(
            @RequestHeader(name = "Authorization") String accessTokenHeader,
            @RequestParam(value = "search", required = false) String parameter,
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to) {
        UUID studentId = securityUtil.validateAuthorization(accessTokenHeader).id();
        if (parameter == null) {
            parameter = "";
        }
        Set<UUID> registeredIdSets = contestService.getRegisteredContestIdsInFromTo(studentId, from, to);
        List<SearchContestDtoForController> contests = contestService.searchContests(parameter, from, to);
        return ResponseEntity.ok(
                new SearchContestsVO(
                        contests.stream().map(
                                contest -> new SearchContestVO(
                                        contest.id(),
                                        contest.name(),
                                        contest.startedAt(),
                                        contest.endedAt(),
                                        registeredIdSets.contains(contest.id())
                                )
                        ).toList()
                )
        );
    }

    @PostMapping("/v1/contest/{contest_id}/register")
    public ResponseEntity<CommonAPIResponseVO> registerContest(
            @RequestHeader(name = "Authorization") String accessToken,
            @RequestBody RegisterContestVO registerContestVo,
            @PathVariable("contest_id") UUID contestId
    ) {
        StudentDto studentDto = securityUtil.validateAuthorization(accessToken);
        contestService.registerContestByUser(contestId, studentDto, registerContestVo.authCode());
        return ResponseEntity.ok(new CommonAPIResponseVO(
                HttpStatus.OK.value(),
                "ok"
        ));
    }

    @GetMapping("/v1/contest/registered")
    public ResponseEntity<RegisteredContestsVO> getRegisteredContestsAboutStudent(@RequestHeader(name = "Authorization") String accessToken) {
        StudentDto studentDto = securityUtil.validateAuthorization(accessToken);
        List<RegisteredContestDto> contests = contestService.getRegisteredContestsInFromTo(studentDto.id());
        return ResponseEntity.ok(new RegisteredContestsVO(mapper.mapToVOList(contests)));
    }
}

