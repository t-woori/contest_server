package com.twoori.contest_server.domain.problem.dto;

import com.twoori.contest_server.domain.problem.dao.ContentCompositeID;
import com.twoori.contest_server.domain.problem.dao.ProblemInContest;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link ProblemInContest}
 */
public record ProblemInContestDto(List<ContentCompositeID> contentContentCompositeIds) implements Serializable {
}