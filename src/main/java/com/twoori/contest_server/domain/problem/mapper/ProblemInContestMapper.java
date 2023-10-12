package com.twoori.contest_server.domain.problem.mapper;

import com.twoori.contest_server.domain.problem.dao.Content;
import com.twoori.contest_server.domain.problem.dao.ProblemInContest;
import com.twoori.contest_server.domain.problem.dto.ProblemInContestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProblemInContestMapper {

    default ProblemInContestDto toDto(ProblemInContest dto) {
        return new ProblemInContestDto(
                dto.getProblem().getContents().stream()
                        .map(Content::getContentCompositeId)
                        .toList()
        );
    }
}
