package com.twoori.contest_server.domain.contest.mapper;

import com.twoori.contest_server.domain.contest.dto.RegisteredContestDto;
import com.twoori.contest_server.domain.contest.vo.RegisteredContestVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContestControllerVOMapper {

    List<RegisteredContestVO> mapToVOList(List<RegisteredContestDto> dtoList);

}
