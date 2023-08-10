package com.twoori.contest_server.domain.student.service;

import com.twoori.contest_server.domain.student.dto.StudentDto;
import com.twoori.contest_server.domain.student.repository.StudentRepository;
import com.twoori.contest_server.global.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public StudentDto getStudentByID(UUID uuid) {
        return StudentDto.daoToDto(studentRepository.findById(uuid).orElseThrow(() -> new NotFoundException("not found student")));
    }


}
