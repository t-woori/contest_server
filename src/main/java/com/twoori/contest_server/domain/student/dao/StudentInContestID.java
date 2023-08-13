package com.twoori.contest_server.domain.student.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;


@Getter
@Embeddable
public class StudentInContestID implements Serializable {
    @Column(name = "student_id")
    private UUID studentID;
    @Column(name = "contest_id")
    private UUID contestID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentInContestID that = (StudentInContestID) o;
        return Objects.equals(studentID, that.studentID) && Objects.equals(contestID, that.contestID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentID, contestID);
    }
}
