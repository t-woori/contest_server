package com.twoori.contest_server.domain.contest.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twoori.contest_server.domain.contest.dao.QContest;
import com.twoori.contest_server.domain.contest.dto.EnterContestDto;
import com.twoori.contest_server.domain.student.dao.QStudentInContest;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

public class ContestRepositoryImpl implements ContestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ContestRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<EnterContestDto> getRegisteredStudentAboutStudent(UUID contestId, UUID studentId) {
        EnterContestDto contestDto = queryFactory.select(
                        Projections.constructor(
                                EnterContestDto.class,
                                QContest.contest.id.as("contestId"),
                                QContest.contest.name.as("name"),
                                QContest.contest.hostName.as("hostName"),
                                QContest.contest.runningStartDateTime.as("startDateTime"),
                                QContest.contest.runningEndDateTime.as("endDateTime")
                        )).from(QStudentInContest.studentInContest)
                .join(QContest.contest)
                .on(QStudentInContest.studentInContest.id.contestID.eq(QContest.contest.id))
                .where(QStudentInContest.studentInContest.id.contestID.eq(contestId),
                        QStudentInContest.studentInContest.id.studentID.eq(studentId))
                .fetchOne();
        if (contestDto == null) {
            return Optional.empty();
        }
        return Optional.of(contestDto);
    }

    @Override
    public boolean isResigned(UUID contestId, UUID studentId) {
        return queryFactory.selectFrom(QStudentInContest.studentInContest)
                .where(QStudentInContest.studentInContest.id.contestID.eq(contestId),
                        QStudentInContest.studentInContest.id.studentID.eq(studentId),
                        QStudentInContest.studentInContest.isResigned.eq(true))
                .fetchFirst() != null;
    }

    @Override
    public boolean isEnteredStudentInContest(UUID id, UUID contestId) {
        return queryFactory.selectOne().from(QStudentInContest.studentInContest)
                .where(QStudentInContest.studentInContest.id.contestID.eq(id),
                        QStudentInContest.studentInContest.id.contestID.eq(contestId))
                .limit(1)
                .fetchFirst() != null;
    }

    @Transactional
    @Override
    public void updateEnterStudentInContest(UUID studentId, UUID contestId) {
        queryFactory.update(QStudentInContest.studentInContest)
                .set(QStudentInContest.studentInContest.isEntered, Expressions.asBoolean(true).isTrue())
                .where(QStudentInContest.studentInContest.id.contestID.eq(contestId),
                        QStudentInContest.studentInContest.id.studentID.eq(studentId))
                .execute();
    }
}
