package com.twoori.contest_server.domain.problem.repository;

import com.twoori.contest_server.domain.problem.dao.LogStudentInProblem;
import com.twoori.contest_server.domain.problem.dao.LogStudentInProblemID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogStudentInProblemRepository extends JpaRepository<LogStudentInProblem, LogStudentInProblemID>, LogStudentInProblemRepositoryCustom {

}