package com.twoori.contest_server.domain.problem.controller;

import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.service.ProblemService;
import com.twoori.contest_server.domain.problem.vo.ContentVO;
import com.twoori.contest_server.domain.problem.vo.ProblemVO;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.global.controller.SecurityController;
import com.twoori.contest_server.global.security.StudentJwtProvider;
import com.twoori.contest_server.global.util.Utils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
}
