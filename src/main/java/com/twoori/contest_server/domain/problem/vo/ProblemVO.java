package com.twoori.contest_server.domain.problem.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.twoori.contest_server.domain.problem.enums.CHAPTER_TYPE;
import com.twoori.contest_server.domain.problem.enums.GRADE;
import com.twoori.contest_server.domain.problem.enums.PROBLEM_TYPE;
import com.twoori.contest_server.global.vo.APIOkMessageVO;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ProblemVO extends APIOkMessageVO {
    private final Long problemId;
    @JsonProperty("type")
    private final PROBLEM_TYPE problemType;
    @JsonProperty("chapter_id")
    private final CHAPTER_TYPE chapterType;
    private final GRADE grade;
    private final String imageURL;
    private final ContentVO content;
    private final Long contentId;

    public ProblemVO(Long problemId,
                     PROBLEM_TYPE problemType,
                     CHAPTER_TYPE chapterType,
                     GRADE grade,
                     String imageURL, ContentVO content,
                     Long contentId) {
        this.problemId = problemId;
        this.problemType = problemType;
        this.chapterType = chapterType;
        this.grade = grade;
        this.imageURL = imageURL;
        this.content = content;
        this.contentId = contentId;
    }
}
