package com.twoori.contest_server.domain.problem.repository;

import com.twoori.contest_server.domain.problem.dao.LogStudentInProblem;
import com.twoori.contest_server.domain.problem.dao.LogStudentInProblemID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface LogStudentInProblemRepository extends JpaRepository<LogStudentInProblem, LogStudentInProblemID> {
    @Query("SELECT l FROM LogStudentInProblem l " +
            "WHERE l.logStudentInProblemId.contestId = :contestId " +
            "AND l.logStudentInProblemId.studentId = :studentId " +
            "AND l.logStudentInProblemId.noOfProblemInContest = " +
            "(" +
            "SELECT MAX(l.logStudentInProblemId.noOfProblemInContest)" +
            " FROM LogStudentInProblem l WHERE " +
            "l.logStudentInProblemId.contestId = :contestId " +
            "AND l.logStudentInProblemId.studentId = :studentId)")
    Optional<LogStudentInProblem> findLastNoOfProblemInContest(
            @Param("contestId") UUID contestId,
            @Param("studentId") UUID studentId);

}