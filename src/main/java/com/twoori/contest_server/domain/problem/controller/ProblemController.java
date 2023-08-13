package com.twoori.contest_server.domain.problem.controller;

import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.service.ProblemService;
import com.twoori.contest_server.domain.problem.vo.ContentVO;
import com.twoori.contest_server.domain.problem.vo.ProblemVO;
import com.twoori.contest_server.domain.problem.vo.UpdateRequestStatusVO;
import com.twoori.contest_server.domain.problem.vo.UpdateResponseStatusVO;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.controller.SecurityController;
import com.twoori.contest_server.global.security.StudentJwtProvider;
import com.twoori.contest_server.global.util.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class ProblemController extends SecurityController {

    private final ProblemService problemService;

    public ProblemController(Utils utils, StudentJwtProvider studentJwtProvider, ProblemService problemService) {
        super(utils, studentJwtProvider);
        this.problemService = problemService;
    }
    @PostMapping("/v1/contest/{contest_id}/quiz")
    public ProblemVO getProblemByStudent(
            @RequestHeader(name = "Authorization") String accessTokenHeader,
            @PathVariable("contest_id") UUID contestId) {
        StudentDto studentDto = super.validateAuthorization(accessTokenHeader);
        ProblemDto dto = problemService.getProblemByStudent(contestId, studentDto.id());
        return new ProblemVO(
                dto.id(),
                dto.problemType(),
                dto.chapterType(),
                dto.grade(),
                dto.imageURL(),
                dto.contents().stream()
                        .map(contentDto -> new ContentVO(contentDto.preScript(),
                                contentDto.question(),
                                contentDto.answer(),
                                contentDto.postScript())).toList()
        );
    }

    @PutMapping("/v1/contest/{contest_id}/student/{student_id}/status")
    public ResponseEntity<UpdateResponseStatusVO> updateQuizStatus(@PathVariable("contest_id") UUID contestId, @PathVariable("student_id") UUID studentId,
                                                                   @RequestBody UpdateRequestStatusVO requestStatusVO) {
        problemService.updateQuizStatus(contestId, studentId, requestStatusVO.problemId());
        return ResponseEntity.ok(new UpdateResponseStatusVO(200, "ok"));
    }
}
