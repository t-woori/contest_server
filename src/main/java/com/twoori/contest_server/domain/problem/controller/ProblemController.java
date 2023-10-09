package com.twoori.contest_server.domain.problem.controller;

import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.mapper.ProblemMapper;
import com.twoori.contest_server.domain.problem.service.ProblemService;
import com.twoori.contest_server.domain.problem.vo.ProblemVO;
import com.twoori.contest_server.domain.problem.vo.ResponseTotalStatusVO;
import com.twoori.contest_server.domain.problem.vo.SolvedProblemVO;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.security.SecurityUtil;
import com.twoori.contest_server.global.vo.APIOkMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
public class ProblemController {

    private final ProblemService problemService;
    private final ProblemMapper mapper;
    private final SecurityUtil securityUtil;

    public ProblemController(ProblemService problemService, ProblemMapper mapper, SecurityUtil securityUtil) {
        this.problemService = problemService;
        this.mapper = mapper;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/v1/contest/{contest_id}/total_status")
    public ResponseEntity<ResponseTotalStatusVO> getTotalStatus(@RequestHeader(name = "Authorization") String accessTokenHeader,
                                                                @PathVariable("contest_id") UUID contestId) {
        StudentDto studentDto = securityUtil.validateAuthorization(accessTokenHeader);
        log.info("[Controller] request total status. contest : {}, student: {}", contestId, studentDto.id());
        List<Long> statues = problemService.getTotalStatus();

        log.info("[Controller] request total status. contest : {}, student: {}", contestId, studentDto.id());
        return ResponseEntity.ok(new ResponseTotalStatusVO(statues, 200, "ok"));
    }

    @GetMapping("/v1/contest/{contest_id}/problem/{problem_id}")
    public ResponseEntity<ProblemVO> getProblem(@PathVariable("contest_id") UUID contestId,
                                                @PathVariable("problem_id") Long problemId,
                                                @RequestHeader(name = "Authorization") String accessTokenHeader) {
        StudentDto studentDto = securityUtil.validateAuthorization(accessTokenHeader);
        log.info("[Controller] request student status. contest : {}, student: {}", contestId, studentDto.id());
        ProblemDto problem = problemService.getProblem(contestId, problemId);
        log.info("[Controller] response student status. contest : {}, student: {}", contestId, studentDto.id());
        return ResponseEntity.ok(mapper.dtoToVo(problem));
    }

    @PutMapping("/v1/contest/{contest_id}/student/{student_id}/problem/score")
    public ResponseEntity<APIOkMessageVO> updateQuizStatus(@PathVariable("contest_id") UUID contestId, @PathVariable("student_id") UUID studentId,
                                                           @RequestBody SolvedProblemVO problemVO) {
        log.info("[Controller] request update quiz status : {}, contest_id: {}, student_id: {}", problemVO, contestId, studentId);
        problemService.updateMaxScoreAboutProblem(mapper.voToSolvedProblemDto(contestId, studentId, problemVO));
        log.info("[Controller] request update quiz status : {}, contest_id: {}, student_id: {}", problemVO, contestId, studentId);
        return ResponseEntity.ok(new APIOkMessageVO());
    }
}
