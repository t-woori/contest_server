package com.twoori.contest_server.domain.student.service;

import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dao.StudentInContestID;
import com.twoori.contest_server.domain.student.dto.ResultContestDto;
import com.twoori.contest_server.domain.student.repository.StudentInContestRepository;
import com.twoori.contest_server.global.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StudentService {
    private final StudentInContestRepository studentInContestRepository;

    public StudentService(StudentInContestRepository studentInContestRepository) {
        this.studentInContestRepository = studentInContestRepository;
    }
    public ResultContestDto getScoreAndRank(UUID contestId, UUID studentId) {
        StudentInContest registeredStudents = studentInContestRepository.findById(
                        new StudentInContestID(studentId, contestId))
                .orElseThrow(() -> new NotFoundException("not found student in contest"));
        return new ResultContestDto(registeredStudents.getStudentScore(), registeredStudents.getStudentRank());
    }

}
