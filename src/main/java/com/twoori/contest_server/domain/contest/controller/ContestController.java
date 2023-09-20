package com.twoori.contest_server.domain.contest.controller;

import com.twoori.contest_server.domain.contest.dto.ContestDto;
import com.twoori.contest_server.domain.contest.dto.EnterContestDtoForController;
import com.twoori.contest_server.domain.contest.service.ContestService;
import com.twoori.contest_server.domain.contest.vo.EnterContestVOAPI;
import com.twoori.contest_server.domain.contest.vo.RegisterContestVO;
import com.twoori.contest_server.domain.contest.vo.SearchContestVO;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.controller.SecurityController;
import com.twoori.contest_server.global.security.StudentJwtProvider;
import com.twoori.contest_server.global.util.Utils;
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
        EnterContestDtoForController result = contestService.enterStudentInContest(studentDto.id(), contestId, now);
        return ResponseEntity.ok(
                new EnterContestVOAPI(
                        result.runningStartDateTime(),
                        result.runningEndDateTime()
                )
        );
    }

    @GetMapping("/v1/contest")
    public ResponseEntity<List<SearchContestVO>> searchContests(
            @RequestParam("student_id") UUID studentId,
            @RequestParam("search") String parameter,
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to) {
        Set<UUID> registeredIdSets = contestService.getRegisteredContestIdsInFromTo(studentId, from, to);
        List<ContestDto> contests = contestService.searchContests(parameter, from, to);
        return ResponseEntity.ok(
                contests.stream()
                        .map(contest -> new SearchContestVO(
                                contest.id(),
                                contest.name(),
                                contest.runningStartDateTime(),
                                contest.runningEndDateTime(),
                                registeredIdSets.contains(contest.id())
                        )).toList()
        );
    }

    @PostMapping("/v1/contest/{contest_id}/register")
    public ResponseEntity<CommonAPIResponseVO> registerContest(
            @RequestHeader(name = "Authorization") String accessToken,
            @RequestBody RegisterContestVO registerContestVo,
            @PathVariable("contest_id") UUID contestId
    ) {
        StudentDto studentDto = super.validateAuthorization(accessToken);
        contestService.registerContestByUser(contestId, studentDto, registerContestVo.authCode());
        return ResponseEntity.ok(new CommonAPIResponseVO(
                HttpStatus.OK.value(),
                "ok"
        ));
    }
}
