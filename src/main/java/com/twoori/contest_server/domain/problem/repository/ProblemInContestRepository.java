package com.twoori.contest_server.domain.problem.repository;

import com.twoori.contest_server.domain.problem.dao.ProblemInContest;
import com.twoori.contest_server.domain.problem.dao.ProblemInContestId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProblemInContestRepository extends JpaRepository<ProblemInContest, ProblemInContestId> {
    List<ProblemInContest> findById_ContestIdOrderByNoOfProblemInContestDesc(UUID contestId);

    List<ProblemInContest> findById_ContestIdOrderByNoOfProblemInContestAsc(UUID contestId);


}