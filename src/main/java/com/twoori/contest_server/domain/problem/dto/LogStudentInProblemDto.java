package com.twoori.contest_server.domain.problem.dto;

import com.twoori.contest_server.domain.contest.dto.ContestDto;
import com.twoori.contest_server.domain.student.dto.StudentDto;

import java.io.Serializable;
import java.time.LocalDateTime;


public record LogStudentInProblemDto(Long noOfProblemInContest, StudentDto student,
                                     ContestDto contest, LocalDateTime startSolveProblemDateTime,
                                     LocalDateTime endSolveProblemDateTime) implements Serializable {
}