package com.twoori.contest_server.domain.problem.repository;

import com.twoori.contest_server.domain.problem.dao.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long>, ProblemRepositoryCustom {

}