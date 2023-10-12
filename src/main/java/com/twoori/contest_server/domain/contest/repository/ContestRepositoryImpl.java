package com.twoori.contest_server.domain.contest.repository;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twoori.contest_server.domain.contest.dao.QContest;
import com.twoori.contest_server.domain.contest.dto.CancelContestDto;
import com.twoori.contest_server.domain.contest.dto.EnterContestDto;
import com.twoori.contest_server.domain.contest.dto.SearchContestDto;
import com.twoori.contest_server.domain.student.dao.QStudentInContest;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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
                        )
                ).from(QStudentInContest.studentInContest)
                .join(QContest.contest)
                .on(QStudentInContest.studentInContest.id.contestID.eq(QContest.contest.id))
                .where(QStudentInContest.studentInContest.id.contestID.eq(contestId),
                        studentIdEq(studentId))
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
                        studentIdEq(studentId),
                        QStudentInContest.studentInContest.isResigned.eq(true))
                .fetchFirst() != null;
    }

    @Override
    public boolean isEnteredStudentInContest(UUID studentId, UUID contestId) {
        return queryFactory.selectOne().from(QStudentInContest.studentInContest)
                .where(QStudentInContest.studentInContest.id.studentID.eq(studentId),
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
                        studentIdEq(studentId))
                .execute();
    }


    @Override
    public Set<UUID> getContestIdSetAboutRegisteredStudent(ContestCondition condition) {
        QStudentInContest studentInContest = QStudentInContest.studentInContest;
        QContest contest = QContest.contest;
        return new HashSet<>(queryFactory
                .select(studentInContest.id.contestID)
                .from(studentInContest)
                .join(contest)
                .on(studentInContest.id.contestID.eq(contest.id))
                .where(
                        studentIdEq(condition.getRegisteredStudentId()),
                        startedAtBetweenFromTo(condition.getFrom(), condition.getTo())
                ).fetch());
    }

    public List<SearchContestDto> searchNotStartedContests(ContestCondition condition) {
        return queryFactory.select(projectionAboutContestDto()).from(QContest.contest)
                .where(
                        startedAtBetweenFromTo(condition.getFrom(), condition.getTo()),
                        parameterEq(condition.getParameter())
                ).fetch();
    }

    private ConstructorExpression<SearchContestDto> projectionAboutContestDto() {
        return Projections.constructor(
                SearchContestDto.class,
                QContest.contest.id.as("contestId"),
                QContest.contest.name.as("name"),
                QContest.contest.hostName.as("hostName"),
                QContest.contest.runningStartDateTime.as("startedAt"),
                QContest.contest.runningEndDateTime.as("endContestDateTime")
        );
    }

    @Override
    public List<SearchContestDto> searchRegisteredContest(ContestCondition condition) {
        QStudentInContest studentInContest = QStudentInContest.studentInContest;
        QContest contest = QContest.contest;
        return queryFactory.select(
                        projectionAboutContestDto()
                ).from(studentInContest)
                .join(contest)
                .on(studentInContest.id.contestID.eq(contest.id))
                .where(studentIdEq(condition.getRegisteredStudentId()),
                        startedAtBetweenFromTo(condition.getFrom(), condition.getTo()))
                .fetch();
    }

    @Transactional
    @Override
    public void cancelContest(UUID contestId, UUID studentId) {
        QStudentInContest qStudentInContest = QStudentInContest.studentInContest;
        queryFactory.update(qStudentInContest)
                .set(qStudentInContest.updatedAt, LocalDateTime.now())
                .set(qStudentInContest.deletedAt, LocalDateTime.now())
                .where(qStudentInContest.id.contestID.eq(contestId),
                        qStudentInContest.id.studentID.eq(studentId))
                .execute();
    }

    @Override
    public Optional<CancelContestDto> getTimesAboutContest(UUID contestId) {
        QContest qContest = QContest.contest;
        CancelContestDto result = queryFactory
                .select(
                        Projections.constructor(
                                CancelContestDto.class,
                                qContest.id.as("contestId"),
                                qContest.runningStartDateTime.as("startDateTime"),
                                qContest.runningEndDateTime.as("endDateTime")
                        )
                ).from(qContest)
                .where(qContest.id.eq(contestId)).fetchOne();
        return Optional.ofNullable(result);
    }

    @Transactional
    @Override
    public void resignContest(UUID studentId, UUID contestId) {
        QStudentInContest qStudentInContest = QStudentInContest.studentInContest;
        queryFactory.update(qStudentInContest)
                .set(qStudentInContest.isResigned, Expressions.asBoolean(true).isTrue())
                .set(qStudentInContest.updatedAt, LocalDateTime.now())
                .where(qStudentInContest.id.contestID.eq(contestId),
                        qStudentInContest.id.studentID.eq(studentId))
                .execute();
    }

    @Override
    public List<SearchContestDto> searchEndOfContests(ContestCondition condition) {
        return queryFactory.select(
                        projectionAboutContestDto()
                ).from(QStudentInContest.studentInContest)
                .join(QContest.contest)
                .on(QStudentInContest.studentInContest.id.contestID.eq(QContest.contest.id))
                .where(
                        studentIdEq(condition.getRegisteredStudentId()),
                        endedAtBetweenFromTo(condition.getFrom(), condition.getTo()),
                        parameterEq(condition.getParameter()))
                .fetch();
    }

    private Predicate parameterEq(String parameter) {
        if (parameter == null) {
            return null;
        }
        return QContest.contest.name.contains(parameter);
    }


    private BooleanExpression studentIdEq(UUID studentId) {
        if (studentId == null) {
            return null;
        }
        return QStudentInContest.studentInContest.id.studentID.eq(studentId);
    }

    private BooleanExpression endedAtBetweenFromTo(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            return null;
        }
        return QContest.contest.runningEndDateTime.between(from, to);
    }

    private Predicate startedAtBetweenFromTo(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            return null;
        }
        return QContest.contest.runningStartDateTime.between(from, to);
    }
}
