package com.twoori.contest_server.domain.contest.mapper;

import com.twoori.contest_server.domain.contest.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContestDtoForControllerMapper {
    @Mapping(source = "contestId", target = "id")
    @Mapping(source = "startDateTime", target = "runningStartDateTime")
    @Mapping(source = "endDateTime", target = "runningEndDateTime")
    EnterContestDtoForController toEnterContestDtoForController(EnterContestDto dto);

    List<SearchContestDtoForController> toSearchDtoForControllerList(List<SearchContestDto> dtoList);

    List<RegisteredContestDto> toRegisteredContestDto(List<SearchContestDto> result);

}
