package com.twoori.contest_server.domain.problem.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twoori.contest_server.domain.problem.dao.QContent;
import com.twoori.contest_server.domain.problem.dao.QProblem;
import com.twoori.contest_server.domain.problem.dao.QProblemInContest;
import com.twoori.contest_server.domain.problem.exceptions.NotFoundProblemException;
import jakarta.persistence.EntityManager;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Validated
public class ProblemRepositoryImpl implements ProblemRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    public ProblemRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public ProblemDto getProblem(UUID contestId, Long noOfProblemInContest) {
        QProblemInContest qProblemInContest = QProblemInContest.problemInContest;
        QProblem qProblem = QProblem.problem;
        QContent qContent = QContent.content;

        List<ProblemDto> dto = jpaQueryFactory
                .select(Projections.constructor(ProblemDto.class,
                        qProblem.id,
                        qProblem.problemType,
                        qProblem.chapterType,
                        qProblem.grade,
                        qProblem.imageURL,
                        Projections.list(
                                Projections.constructor(ContentDto.class,
                                        qContent.contentCompositeId.contentId,
                                        qContent.answer,
                                        qContent.preScript,
                                        qContent.question,
                                        qContent.postScript,
                                        qContent.hint
                                )
                        )))
                .from(qProblemInContest)
                .join(qProblem)
                .on(qProblemInContest.id.problemId.eq(qProblem.id))
                .join(qContent)
                .on(qContent.contentCompositeId.problemId.eq(qProblem.id))
                .where(
                        qProblemInContest.id.contestId.eq(contestId),
                        qProblemInContest.noOfProblemInContest.eq(noOfProblemInContest)
                ).fetch();
        if (dto == null || dto.size() == 0) {
            throw new NotFoundProblemException(contestId, noOfProblemInContest);
        }
        return dto.get(0);
    }

}
