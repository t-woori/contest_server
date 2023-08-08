package com.twoori.contest_server.domain.student.repository;

import com.twoori.contest_server.domain.student.dao.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
}