package com.twoori.contest_server.domain.contest.controller;

import com.twoori.contest_server.domain.contest.dto.ContestDto;
import com.twoori.contest_server.domain.contest.service.ContestService;
import com.twoori.contest_server.domain.contest.service.EnterContestDtoForController;
import com.twoori.contest_server.domain.contest.vo.ContestVO;
import com.twoori.contest_server.domain.contest.vo.ContestsVo;
import com.twoori.contest_server.domain.contest.vo.EnterContestVOAPI;
import com.twoori.contest_server.domain.contest.vo.RegisterContestVO;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.controller.SecurityController;
import com.twoori.contest_server.global.security.StudentJwtProvider;
import com.twoori.contest_server.global.util.Utils;
import com.twoori.contest_server.global.vo.CommonAPIResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
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
    public ResponseEntity<ContestsVo> searchContests(@RequestParam("search") String parameter) {
        List<ContestDto> dtos = contestService.searchContests(parameter);
        return ResponseEntity.ok(
                new ContestsVo(dtos.stream()
                        .map(dto -> new ContestVO(
                                dto.id(),
                                dto.name(),
                                dto.hostName(),
                                dto.runningStartDateTime(),
                                dto.runningEndDateTime()
                        )).toList())
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
