package com.twoori.contest_server.domain.problem.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.twoori.contest_server.domain.problem.enums.CHAPTER_TYPE;
import com.twoori.contest_server.domain.problem.enums.GRADE;
import com.twoori.contest_server.domain.problem.enums.PROBLEM_TYPE;
import com.twoori.contest_server.global.vo.APIOkMessageVO;
import lombok.Getter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ProblemVO extends APIOkMessageVO {
    @JsonProperty("id")
    private final Long problemId;
    @JsonProperty("type")
    private final PROBLEM_TYPE problemType;
    @JsonProperty("chapter_id")
    private final CHAPTER_TYPE chapterType;
    private final GRADE grade;
    private final String imageURL;
    private final List<ContentVO> content;

    /**
     * 빈칸 채우기 문제 생성자
     *
     * @param problemId
     * @param problemType
     * @param chapterType
     * @param imageURL
     * @param content
     */
    public ProblemVO(Long problemId, PROBLEM_TYPE problemType, CHAPTER_TYPE chapterType, GRADE grade, String imageURL, List<ContentVO> content) {
        this.problemId = problemId;
        this.problemType = problemType;
        this.chapterType = chapterType;
        this.grade = grade;
        this.imageURL = imageURL;
        this.content = content;
    }

}
