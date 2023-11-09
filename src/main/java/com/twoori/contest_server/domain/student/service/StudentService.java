package com.twoori.contest_server.domain.student.service;

import com.twoori.contest_server.domain.student.dao.StudentInContestID;
import com.twoori.contest_server.domain.student.dto.ResultContestDto;
import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.domain.student.mapper.StudentInContestMapper;
import com.twoori.contest_server.domain.student.repository.StudentInContestRepository;
import com.twoori.contest_server.domain.student.repository.StudentRepository;
import com.twoori.contest_server.global.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentInContestRepository studentInContestRepository;
    private final StudentInContestMapper studentInContestMapper;

    public StudentService(StudentRepository studentRepository, StudentInContestRepository studentInContestRepository,
                          StudentInContestMapper studentInContestMapper) {
        this.studentRepository = studentRepository;
        this.studentInContestRepository = studentInContestRepository;
        this.studentInContestMapper = studentInContestMapper;
    }
    public StudentDto getStudentByID(UUID uuid) {
        return StudentDto.daoToDto(studentRepository.findById(uuid).orElseThrow(() -> new NotFoundException("not found student")));
    }

    public ResultContestDto getScoreAndRank(UUID contestId, UUID studentId) {
        return studentInContestMapper.toDto(studentInContestRepository.findById(
                        new StudentInContestID(studentId, contestId))
                .orElseThrow(() -> new NotFoundException("not found student in contest")));
    }

}
