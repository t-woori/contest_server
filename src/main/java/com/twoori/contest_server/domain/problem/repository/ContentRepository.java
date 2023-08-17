package com.twoori.contest_server.domain.problem.repository;

import com.twoori.contest_server.domain.problem.dao.Content;
import com.twoori.contest_server.domain.problem.dao.ContentCompositeID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, ContentCompositeID> {
    long countByContentCompositeId_ProblemId(Long problemId);

}