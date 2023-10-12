package com.twoori.contest_server.domain.problem.dto;

import com.twoori.contest_server.domain.student.dto.StudentInContestIdDto;

public record UpdateProblemCountDto(StudentInContestIdDto studentInContestIdDto, ProblemIdDto problemIdDto) {
    public String getTotalStatusRedisTemplateKey() {
        return problemIdDto.problemId() + "_" + problemIdDto.contentId();
    }

    public String getStudentRedisTemplateKey() {
        return studentInContestIdDto.contestId().toString() + "_" + studentInContestIdDto.studentID();
    }
}
