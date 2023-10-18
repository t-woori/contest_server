package com.twoori.contest_server.domain.problem.dto;

import com.twoori.contest_server.domain.student.dto.StudentInContestIdDto;

public record UpdateProblemCountDto(StudentInContestIdDto studentInContestIdDto, ProblemIdDto problemIdDto) {
}
