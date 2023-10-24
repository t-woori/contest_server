package com.twoori.contest_server.domain.student.repository;

import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dao.StudentInContestID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface StudentInContestRepository extends JpaRepository<StudentInContest, StudentInContestID> {
    Optional<StudentInContest> findById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTimeAfter(UUID studentID, LocalDateTime runningEndDateTime);

}