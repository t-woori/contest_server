package com.twoori.contest_server.domain.student.dao;

import com.twoori.contest_server.domain.student.dto.StudentInContestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentInContestMapper {
    @Mapping(source = "id.contestID", target = "contestId")
    @Mapping(source = "id.studentID", target = "studentId")
    StudentInContestDto toDto(StudentInContest studentInContest);

}