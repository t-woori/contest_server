package com.twoori.contest_server.domain.contest.controller;

import com.twoori.contest_server.domain.contest.dto.EnterContestDtoForController;
import com.twoori.contest_server.domain.contest.dto.RegisteredContestDto;
import com.twoori.contest_server.domain.contest.dto.SearchContestDto;
import com.twoori.contest_server.domain.contest.dto.SearchContestDtoForController;
import com.twoori.contest_server.domain.contest.mapper.ContestControllerVOMapper;
import com.twoori.contest_server.domain.contest.service.ContestService;
import com.twoori.contest_server.domain.contest.vo.*;
import com.twoori.contest_server.domain.problem.service.ProblemService;
import com.twoori.contest_server.domain.student.dto.StudentInContestIdDto;
import com.twoori.contest_server.domain.student.service.TrackingStudentService;
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
    private final ContestControllerVOMapper mapper;
    private final ProblemService problemService;

    private final TrackingStudentService trackingStudentService;

    public ContestController(ContestService contestService, ContestControllerVOMapper mapper, ProblemService problemService, TrackingStudentService trackingStudentService) {
        this.contestService = contestService;
        this.mapper = mapper;
        this.problemService = problemService;
        this.trackingStudentService = trackingStudentService;
    }

    @GetMapping("/contest/{contest_id}/enter")
    public ResponseEntity<EnterContestVOAPI> requestEnterContest(
            @RequestParam("student_id") UUID studentId,
            @PathVariable("contest_id") UUID contestId) {
        LocalDateTime now = LocalDateTime.now();
        EnterContestDtoForController result = contestService.enterStudentInContest(studentId, contestId, now);
        return ResponseEntity.ok(
                new EnterContestVOAPI(
                        result.runningStartDateTime(),
                        result.runningEndDateTime()
                )
        );
    }

    @GetMapping("/contest")
    public ResponseEntity<SearchContestsVO> searchContests(
            @RequestParam("student_id") UUID studentId,
            @RequestParam(value = "search", required = false) String parameter,
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to) {
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

    @PostMapping("/contest/{contest_id}/register")
    public ResponseEntity<CommonAPIResponseVO> registerContest(
            @RequestParam("student_id") UUID studentId,
            @RequestBody RegisterContestVO registerContestVo,
            @PathVariable("contest_id") UUID contestId
    ) {
        contestService.registerContestByUser(contestId, studentId, registerContestVo.authCode());
        return ResponseEntity.ok(new CommonAPIResponseVO(
                HttpStatus.OK.value(),
                "ok"
        ));
    }

    @GetMapping("/contest/registered")
    public ResponseEntity<RegisteredContestsVO> getRegisteredContestsAboutStudent(
            @RequestParam("student_id") UUID studentId) {
        List<RegisteredContestDto> contests = contestService.searchContestForEnterContest(studentId);
        return ResponseEntity.ok(new RegisteredContestsVO(mapper.mapToVOList(contests)));
    }

    @PutMapping("/contest/{contest_id}/cancel")
    public ResponseEntity<APIOkMessageVO> cancelContest(
            @RequestParam("student_id") UUID studentId,
            @PathVariable("contest_id") UUID contestId
    ) {
        contestService.cancelContest(contestId, studentId, LocalDateTime.now());
        return ResponseEntity.ok(new APIOkMessageVO());
    }

    @PutMapping("/contest/{contest_id}/resign")
    public ResponseEntity<APIOkMessageVO> resignContest(
            @RequestParam("student_id") UUID studentId,
            @PathVariable("contest_id") UUID contestId
    ) {
        log.info("request resign contest, contestId: {}, studentId: {}", contestId, studentId);
        contestService.resignContest(contestId, studentId);
        log.info("trackingStudentService will remove log contest, contestId: {}, studentId: {}", contestId, studentId);
        trackingStudentService.quitContest(new StudentInContestIdDto(contestId, studentId));
        return ResponseEntity.ok(new APIOkMessageVO());
    }

    @GetMapping("/contests/end")
    public ResponseEntity<ContestsVO> getEndOfContests(
            @RequestParam("student_id") UUID studentId
    ) {
        List<SearchContestDto> contests = contestService.searchEndOfContests(studentId);
        return ResponseEntity.ok(new ContestsVO(mapper.mapToListContestVO(contests)));
    }

    @PutMapping("/contest/{contest_id}/end")
    public ResponseEntity<EndContestVO> endContest(
            @RequestParam("student_id") UUID studentId,
            @PathVariable("contest_id") UUID contestId
    ) {
        LocalDateTime endDateTime = LocalDateTime.now();
        long diffTime = contestService.endingContest(contestId, studentId, endDateTime);
        double average = problemService.createAverageScore(contestId, studentId);
        trackingStudentService.quitContest(new StudentInContestIdDto(contestId, studentId));
        return ResponseEntity.ok(new EndContestVO(average, diffTime));
    }
}

