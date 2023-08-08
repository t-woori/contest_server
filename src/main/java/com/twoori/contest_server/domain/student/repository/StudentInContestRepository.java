package com.twoori.contest_server.domain.student.repository;

import com.twoori.contest_server.domain.contest.dao.Contest;
import com.twoori.contest_server.domain.student.dao.StudentInContest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentInContestRepository extends JpaRepository<StudentInContest, Contest> {
    Optional<StudentInContest> findByContest_IdAndStudent_Id(UUID contestID, UUID studentID);

}