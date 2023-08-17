package com.twoori.contest_server.domain.problem.repository;

import com.twoori.contest_server.domain.problem.dao.ProblemInContest;
import com.twoori.contest_server.domain.problem.dao.ProblemInContestId;
import com.twoori.contest_server.domain.problem.dto.ProblemInContestDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProblemInContestRepository extends JpaRepository<ProblemInContest, ProblemInContestId> {
    Optional<ProblemInContestDto> findById_ContestIdAndNoOfProblemInContest(UUID contestId, Long noOfProblemInContest);



}