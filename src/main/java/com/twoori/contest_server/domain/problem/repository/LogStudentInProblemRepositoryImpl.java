package com.twoori.contest_server.domain.problem.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twoori.contest_server.domain.problem.dao.QContent;
import com.twoori.contest_server.domain.problem.dao.QLogStudentInProblem;
import com.twoori.contest_server.domain.problem.dao.QProblem;
import com.twoori.contest_server.domain.problem.dao.QProblemInContest;
import com.twoori.contest_server.domain.problem.dto.LogStudentInProblemIdDto;
import com.twoori.contest_server.domain.problem.dto.QuizScoreDto;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class LogStudentInProblemRepositoryImpl implements LogStudentInProblemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public LogStudentInProblemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Optional<QuizScoreDto> findByNonPassedProblem(LogStudentInProblemIdDto logStudentInProblemIdDto,
                                                         double conditionPassProblem) {
        QLogStudentInProblem qLog = QLogStudentInProblem.logStudentInProblem;
        QProblemInContest qProblemInContest = QProblemInContest.problemInContest;
        QProblem qProblem = QProblem.problem;
        QContent qContent = QContent.content;
        QuizScoreDto queryResult = queryFactory
                .select(Projections.constructor(QuizScoreDto.class,
                        qLog.logStudentInProblemId.contestId.as("contestId"),
                        qLog.logStudentInProblemId.studentId.as("studentId"),
                        qLog.logStudentInProblemId.problemId.as("problemId"),
                        qLog.logStudentInProblemId.contentId.as("contentId"),
                        qLog.score.as("score")
                )).from(qLog)
                .join(qProblemInContest)
                .on(qProblemInContest.id.problemId.eq(qLog.logStudentInProblemId.problemId),
                        qProblemInContest.id.contestId.eq(qLog.logStudentInProblemId.contestId))
                .join(qProblem)
                .on(qProblem.id.eq(qLog.logStudentInProblemId.problemId))
                .join(qContent)
                .on(qProblem.id.eq(qContent.problem.id),
                        qContent.contentCompositeId.contentId.eq(qLog.logStudentInProblemId.contentId)).
                where(qLog.logStudentInProblemId.contestId.eq(logStudentInProblemIdDto.contestId()),
                        qLog.logStudentInProblemId.studentId.eq(logStudentInProblemIdDto.studentId()),
                        qLog.logStudentInProblemId.problemId.eq(logStudentInProblemIdDto.problemId()),
                        qLog.logStudentInProblemId.contentId.eq(logStudentInProblemIdDto.contentId()),
                        qLog.score.lt(conditionPassProblem))
                .fetchOne();
        return Optional.ofNullable(queryResult);
    }

}
