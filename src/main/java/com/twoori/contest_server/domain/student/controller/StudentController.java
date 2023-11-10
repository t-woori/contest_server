package com.twoori.contest_server.domain.student.controller;

import com.twoori.contest_server.domain.contest.dto.EnterContestDto;
import com.twoori.contest_server.domain.contest.service.ContestService;
import com.twoori.contest_server.domain.problem.dto.ProblemIdDto;
import com.twoori.contest_server.domain.problem.service.ProblemService;
import com.twoori.contest_server.domain.student.dto.ResultContestDto;
import com.twoori.contest_server.domain.student.dto.StudentInContestIdDto;
import com.twoori.contest_server.domain.student.service.StudentService;
import com.twoori.contest_server.domain.student.service.TrackingStudentService;
import com.twoori.contest_server.domain.student.vo.ContestStatus;
import com.twoori.contest_server.domain.student.vo.ProblemStatus;
import com.twoori.contest_server.domain.student.vo.ResultScoreVo;
import com.twoori.contest_server.domain.student.vo.StudentStatusVO;
import com.twoori.contest_server.global.exception.BadRequestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
public class StudentController {

    private final ContestService contestService;
    private final TrackingStudentService trackingStudentService;
    private final ProblemService problemService;
    private final StudentService studentService;

    public StudentController(ContestService contestService,
                             TrackingStudentService trackingStudentService,
                             ProblemService problemService, StudentService studentService) {
        this.contestService = contestService;
        this.trackingStudentService = trackingStudentService;
        this.problemService = problemService;
        this.studentService = studentService;
    }

    @GetMapping("/v1/contest/student/status")
    public StudentStatusVO getStudentStatus(@RequestParam("student_id") UUID studentId) {
        EnterContestDto contestDto = contestService.findContestIdAboutEnterableContest(studentId, LocalDateTime.now());
        StudentInContestIdDto studentInContestID = new StudentInContestIdDto(contestDto.contestId(), studentId);
        ProblemIdDto status = trackingStudentService.getStudentStatusInContest(studentInContestID);
        int countOfTry = problemService.getCountOfTry(studentInContestID, status);
        return new StudentStatusVO(new ContestStatus(
                contestDto.contestId(), contestDto.startDateTime(), contestDto.endDateTime()
                , new ProblemStatus(status.problemId(), status.contentId(), countOfTry)));
    }

    @GetMapping("/v1/contest/{contest_id}/student/score")
    public ResultScoreVo getScore(
            @PathVariable("contest_id") UUID contestId,
            @RequestParam("student_id") UUID studentId) {
        ResultContestDto result = studentService.getScoreAndRank(contestId, studentId);
        long totalGrade = contestService.countTotalStudents(contestId);
        if (result.rank() == 0 || result.score() == null) {
            throw new BadRequestException("scoring score");
        }
        return new ResultScoreVo(result.score(), result.rank(), totalGrade);
    }

}
