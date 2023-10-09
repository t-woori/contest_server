package com.twoori.contest_server.domain.problem.mapper;

import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.dto.SolvedProblemDto;
import com.twoori.contest_server.domain.problem.vo.ProblemVO;
import com.twoori.contest_server.domain.problem.vo.SolvedProblemVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ProblemMapper {

    ProblemVO dtoToVo(ProblemDto problemDto);

    @Mapping(source = "problemVO.problemId", target = "noOfProblemInContest")
    @Mapping(source = "problemVO.score", target = "newScore")
    SolvedProblemDto voToSolvedProblemDto(UUID contestId, UUID studentId, SolvedProblemVO problemVO);
}
