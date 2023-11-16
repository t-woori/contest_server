package com.twoori.contest_server.domain.contest.mapper;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.contest.dto.ContestDto;
import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dto.StudentInContestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RepositoryMapper {
    default StudentInContestDto toStudentInContestDto(StudentInContest studentInContest) {
        Contest contest = studentInContest.getContest();
        return new StudentInContestDto(
                studentInContest.getId().getContestID(), studentInContest.getId().getStudentID(),
                contest.getRunningStartDateTime(), contest.getRunningEndDateTime(), studentInContest.getEndContestAt());
    }


    ContestDto toContestDto(Contest contest);
}
