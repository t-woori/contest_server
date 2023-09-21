package com.twoori.contest_server.domain.problem.controller;

import com.twoori.contest_server.domain.problem.dto.ContentDtoForController;
import com.twoori.contest_server.domain.problem.dto.MinInfoAboutStudentAndContestDto;
import com.twoori.contest_server.domain.problem.dto.QuizScoreDto;
import com.twoori.contest_server.domain.problem.service.ProblemService;
import com.twoori.contest_server.domain.problem.vo.*;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
public class ProblemController {

    private final ProblemService problemService;
    private final SecurityUtil securityUtil;

    public ProblemController(ProblemService problemService, SecurityUtil securityUtil) {
        this.problemService = problemService;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/v1/contest/{contest_id}/quiz")
    public ProblemVO getProblemByStudent(
            @RequestHeader(name = "Authorization") String accessTokenHeader,
            @PathVariable("contest_id") UUID contestId) {
        StudentDto studentDto = securityUtil.validateAuthorization(accessTokenHeader);
        ContentDtoForController dto = problemService.getNotSolvedProblemByStudent(
                new MinInfoAboutStudentAndContestDto(contestId, studentDto.id())
        );
        return new ProblemVO(
                dto.problemDtoForController().problemId(),
                dto.problemDtoForController().problemType(),
                dto.problemDtoForController().chapterType(),
                dto.problemDtoForController().grade(),
                dto.problemDtoForController().imageURL(),
                new ContentVO(
                        dto.answer(),
                        dto.preScript(),
                        dto.question(),
                        dto.postScript(),
                        dto.hint()
                ),
                dto.contentID()
        );
    }

    @PutMapping("/v1/contest/{contest_id}/student/{student_id}/status")
    public ResponseEntity<ResponseUpdateStatusVO> updateQuizStatus(@PathVariable("contest_id") UUID contestId, @PathVariable("student_id") UUID studentId,
                                                                   @RequestBody RequestUpdateStatusVO ids) {
        log.info("[Controller] request update quiz status : {}", ids);
        problemService.updateQuizScore(new QuizScoreDto(
                contestId,
                studentId,
                ids.problemId(),
                ids.contentId(),
                ids.score()
        ));
        log.info("[Controller] response update quiz status : {}", ids);
        return ResponseEntity.ok(new ResponseUpdateStatusVO(200, "ok"));
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
}
