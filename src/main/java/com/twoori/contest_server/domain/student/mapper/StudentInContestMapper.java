package com.twoori.contest_server.domain.student.mapper;

import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dto.ResultContestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentInContestMapper {
    @Mapping(target = "score", source = "studentScore")
    @Mapping(target = "rank", source = "studentRank")
    ResultContestDto toDto(StudentInContest studentInContest);
}