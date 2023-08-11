package com.twoori.contest_server.domain.problem.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ContentID implements Serializable {
    @Column(name = "problem_id")
    private Integer problemID;
    @Column(name = "content_id")
    private Long contentID;

}
