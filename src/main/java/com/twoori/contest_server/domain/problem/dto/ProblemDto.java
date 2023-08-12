package com.twoori.contest_server.domain.problem.dto;

import com.twoori.contest_server.domain.problem.enums.CHAPTER_TYPE;
import com.twoori.contest_server.domain.problem.enums.GRADE;
import com.twoori.contest_server.domain.problem.enums.PROBLEM_TYPE;

import java.io.Serializable;
import java.util.List;


public record ProblemDto(Long id, String imageURL, GRADE grade, PROBLEM_TYPE problemType,
                         List<ContentDto> contents, CHAPTER_TYPE chapterType) implements Serializable {
}