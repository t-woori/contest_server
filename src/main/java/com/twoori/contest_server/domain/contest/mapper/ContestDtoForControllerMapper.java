package com.twoori.contest_server.domain.contest.mapper;

import com.twoori.contest_server.domain.contest.dto.EnterContestDto;
import com.twoori.contest_server.domain.contest.dto.EnterContestDtoForController;
import com.twoori.contest_server.domain.contest.dto.SearchContestDto;
import com.twoori.contest_server.domain.contest.dto.SearchContestDtoForController;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContestDtoForControllerMapper {
    @Mapping(source = "contestId", target = "id")
    @Mapping(source = "startDateTime", target = "runningStartDateTime")
    @Mapping(source = "endDateTime", target = "runningEndDateTime")
    EnterContestDtoForController toEnterContestDtoForController(EnterContestDto repositoryDto);

    @Mapping(source = "runningStartDateTime", target = "startedAt")
    @Mapping(source = "runningEndDateTime", target = "endedAt")
    SearchContestDtoForController toSearchDtoForController(SearchContestDto searchContestDto);
}
