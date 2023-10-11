package com.twoori.contest_server.domain.student.repository;

import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dao.StudentInContestID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentInContestRepository extends JpaRepository<StudentInContest, StudentInContestID> {

}