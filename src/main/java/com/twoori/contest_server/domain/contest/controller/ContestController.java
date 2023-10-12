package com.twoori.contest_server.domain.contest.controller;

import com.twoori.contest_server.domain.contest.dto.*;
import com.twoori.contest_server.domain.contest.mapper.ContestControllerVOMapper;
import com.twoori.contest_server.domain.contest.service.ContestService;
import com.twoori.contest_server.domain.contest.vo.*;
import com.twoori.contest_server.domain.problem.service.ProblemService;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.security.SecurityUtil;
import com.twoori.contest_server.global.vo.APIOkMessageVO;
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
    private final ProblemService problemService;

    public ContestController(ContestService contestService, SecurityUtil securityUtil, ContestControllerVOMapper mapper, ProblemService problemService) {
        this.contestService = contestService;
        this.securityUtil = securityUtil;
        this.mapper = mapper;
        this.problemService = problemService;
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
                                        contest.contestId(),
                                        contest.name(),
                                        contest.startedAt(),
                                        contest.endedAt(),
                                        registeredIdSets.contains(contest.contestId())
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
        List<RegisteredContestDto> contests = contestService.searchContestForEnterContest(studentDto.id());
        return ResponseEntity.ok(new RegisteredContestsVO(mapper.mapToVOList(contests)));
    }

    @PutMapping("/v1/contest/{contest_id}/cancel")
    public ResponseEntity<APIOkMessageVO> cancelContest(
            @RequestHeader(name = "Authorization") String accessToken,
            @PathVariable("contest_id") UUID contestId
    ) {
        StudentDto studentDto = securityUtil.validateAuthorization(accessToken);
        contestService.cancelContest(contestId, studentDto.id(), LocalDateTime.now());
        return ResponseEntity.ok(new APIOkMessageVO());
    }

    @PutMapping("/v1/contest/{contest_id}/resign")
    public ResponseEntity<APIOkMessageVO> resignContest(
            @RequestHeader(name = "Authorization") String accessToken,
            @PathVariable("contest_id") UUID contestId
    ) {
        StudentDto studentDto = securityUtil.validateAuthorization(accessToken);
        log.info("request resign contest, contestId: {}, studentId: {}", contestId, studentDto.id());
        contestService.resignContest(contestId, studentDto.id());
        log.info("resign contest, contestId: {}, studentId: {}", contestId, studentDto.id());
        return ResponseEntity.ok(new APIOkMessageVO());
    }

    @GetMapping("/v1/contests/end")
    public ResponseEntity<ContestsVO> getEndOfContests(
            @RequestHeader(name = "Authorization") String accessToken
    ) {
        StudentDto studentDto = securityUtil.validateAuthorization(accessToken);
        List<SearchContestDto> contests = contestService.searchEndOfContests(studentDto.id());
        return ResponseEntity.ok(new ContestsVO(mapper.mapToListContestVO(contests)));
    }

    @PutMapping("/v1/contest/{contest_id}/end")
    public ResponseEntity<EndContestVO> endContest(
            @RequestHeader(name = "Authorization") String accessToken,
            @PathVariable("contest_id") UUID contestId
    ) {
        LocalDateTime endDateTime = LocalDateTime.now();
        StudentDto studentDto = securityUtil.validateAuthorization(accessToken);
        EndContestDto dto = contestService.endingContest(contestId, studentDto.id(), endDateTime);
        double average = problemService.createAverageScore(contestId, studentDto.id());
        return ResponseEntity.ok(mapper.toEndContestDto(dto, average));
    }
}

