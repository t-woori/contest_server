package com.twoori.contest_server.domain.problem.mapper;

import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.vo.ProblemVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProblemMapper {

    ProblemVO dtoToVo(ProblemDto problemDto);
}
