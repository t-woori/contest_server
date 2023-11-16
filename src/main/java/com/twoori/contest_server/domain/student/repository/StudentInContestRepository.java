package com.twoori.contest_server.domain.student.repository;

import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dao.StudentInContestID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface StudentInContestRepository extends JpaRepository<StudentInContest, StudentInContestID> {
    @Query("select count(s) from StudentInContest s where s.id.contestID = ?1")
    long countById_ContestID(UUID contestID);

    Optional<StudentInContest> findById_StudentIDAndIsEnteredTrueAndIsResignedFalseAndContest_RunningEndDateTimeAfter(UUID studentID, LocalDateTime runningEndDateTime);

    @Query(value = "select * from student_in_contest s where s.contest_id = ?1  and s.student_id = ?2", nativeQuery = true)
    Optional<StudentInContest> findByIdIncludeSoftDelete(UUID contestID, UUID studentId);
}