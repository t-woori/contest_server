package com.twoori.contest_server.domain.problem.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twoori.contest_server.domain.problem.dao.LogStudentInProblemID;
import com.twoori.contest_server.domain.problem.dao.QLogStudentInProblem;
import jakarta.persistence.EntityManager;
import org.springframework.cache.annotation.Cacheable;

public class LogStudentInProblemRepositoryImpl implements LogStudentInProblemRepositoryCustom {

    private static final QLogStudentInProblem qLogStudentInProblem = QLogStudentInProblem.logStudentInProblem;
    private final JPAQueryFactory queryFactory;

    public LogStudentInProblemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Integer countLatestSolvedProblem(LogStudentInProblemID logStudentInProblemID) {
        Integer result = queryFactory.select(qLogStudentInProblem.logStudentInProblemId.countOfTry.max())
                .from(qLogStudentInProblem)
                .where(logStudentInProblemIdExcludeCountOfTryEq(logStudentInProblemID))
                .fetchOne();
        if (result == null) {
            return 0;
        }
        return result;
    }


    private Predicate logStudentInProblemIdExcludeCountOfTryEq(LogStudentInProblemID logStudentInProblemID) {
        return Expressions.allOf(
                qLogStudentInProblem.logStudentInProblemId.contestId.eq(logStudentInProblemID.getContestId()),
                qLogStudentInProblem.logStudentInProblemId.studentId.eq(logStudentInProblemID.getStudentId()),
                qLogStudentInProblem.logStudentInProblemId.problemId.eq(logStudentInProblemID.getProblemId()),
                qLogStudentInProblem.logStudentInProblemId.contentId.eq(logStudentInProblemID.getContentId())
        );
    }

    @Cacheable(value = "max_score", key = "#logStudentInProblemID.getNoOfProblemInContest().toString()+'_'+" +
            "#logStudentInProblemID.getContentId()+'_'+" + "#logStudentInProblemID.getContestId()+'_' +#logStudentInProblemID.getStudentId()")
    @Override
    public Double getMaxScoreProblemOne(LogStudentInProblemID logStudentInProblemID) {
        return queryFactory.select(qLogStudentInProblem.score.max())
                .from(qLogStudentInProblem)
                .where(logStudentInProblemIdExcludeCountOfTryEq(logStudentInProblemID))
                .fetchOne();
    }
}
