package com.twoori.contest_server.domain.problem.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class ContentCompositeID implements Serializable {
    @Column(name = "problem_id")
    private Long problemId;
    @Column(name = "content_id")
    private Long contentId;

    public ContentCompositeID(Long problemId, Long contentId) {
        this.problemId = problemId;
        this.contentId = contentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentCompositeID contentCompositeID1 = (ContentCompositeID) o;
        return Objects.equals(problemId, contentCompositeID1.problemId) && Objects.equals(contentId, contentCompositeID1.contentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemId, contentId);
    }
}
