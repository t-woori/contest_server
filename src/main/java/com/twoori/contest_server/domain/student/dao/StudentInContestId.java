package com.twoori.contest_server.domain.student.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;


@Embeddable
public class StudentInContestId implements Serializable {
    @Column(name = "student_id")
    private UUID student_id;
    @Column(name = "contest_id")
    private UUID contest_id;

}
