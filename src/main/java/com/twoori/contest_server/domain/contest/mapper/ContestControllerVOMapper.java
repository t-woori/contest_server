package com.twoori.contest_server.domain.contest.mapper;

import com.twoori.contest_server.domain.contest.dto.RegisteredContestDto;
import com.twoori.contest_server.domain.contest.dto.SearchContestDto;
import com.twoori.contest_server.domain.contest.vo.ContestVO;
import com.twoori.contest_server.domain.contest.vo.RegisteredContestVO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContestControllerVOMapper {

    @Named("mappingRegisteredContestVO")
    @Mapping(target = "id", source = "dto.contestId")
    RegisteredContestVO mapToRegisteredContestVO(RegisteredContestDto dto);

    @IterableMapping(qualifiedByName = "mappingRegisteredContestVO")
    List<RegisteredContestVO> mapToVOList(List<RegisteredContestDto> dtoList);

    List<ContestVO> mapToListContestVO(List<SearchContestDto> dtoList);
}
