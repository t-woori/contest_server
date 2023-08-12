package com.twoori.contest_server.domain.problem.service;

import com.twoori.contest_server.domain.problem.dao.Problem;
import com.twoori.contest_server.domain.problem.dto.ContentDto;
import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.dto.ProblemInContestDto;
import com.twoori.contest_server.domain.problem.repository.ProblemInContestRepository;
import com.twoori.contest_server.domain.problem.repository.ProblemRepository;
import com.twoori.contest_server.domain.student.dao.LogStudentInProblem;
import com.twoori.contest_server.domain.student.dao.LogStudentInProblemID;
import com.twoori.contest_server.domain.student.dao.LogStudentInProblemRepository;
import com.twoori.contest_server.global.exception.OKException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final LogStudentInProblemRepository logStudentInProblemRepository;
    private final ProblemInContestRepository problemInContestRepository;

    private final long LAST_PROBLEM_NO_IN_CONTEST = 9L;
    private final long NOT_FOUND_SOLVING_PROBLEM = -1L;

    public ProblemService(ProblemRepository problemRepository, LogStudentInProblemRepository logStudentInProblemRepository, ProblemInContestRepository problemInContestRepository) {
        this.problemRepository = problemRepository;
        this.logStudentInProblemRepository = logStudentInProblemRepository;
        this.problemInContestRepository = problemInContestRepository;
    }

    public ProblemDto getProblemByStudent(UUID contestId, UUID studentId) {
        Long lastProblemNo = getLastProblemNo(contestId, studentId);
        if (lastProblemNo >= LAST_PROBLEM_NO_IN_CONTEST) {
            throw new OKException("end contest");
        }
        return getLastProblemByStudentInContest(contestId, studentId, lastProblemNo);
    }

    private Long getLastProblemNo(UUID contestId, UUID studentId) {
        return logStudentInProblemRepository.findLastNoOfProblemInContest(contestId, studentId)
                .map(logStudentInProblem -> logStudentInProblem.getLogStudentInProblemId().getNoOfProblemInContest())
                .orElse(NOT_FOUND_SOLVING_PROBLEM);
    }


    private ProblemDto getLastProblemByStudentInContest(UUID contestId, UUID studentId, Long lastProblemNo) {
        List<ProblemInContestDto> problemLists = problemInContestRepository.findById_ContestIdOrderByNoOfProblemInContestAsc(contestId)
                .stream().map(problemInContest -> new ProblemInContestDto(problemInContest.getId().getProblemId(),
                        problemInContest.getId().getContestId(), problemInContest.getNoOfProblemInContest()))
                .toList();
        Long nextProblemID = problemLists.get(lastProblemNo.intValue() + 1).problemID();

        Problem problemDao = problemRepository.findById(nextProblemID)
                .orElseThrow(() -> new IllegalArgumentException("not found problem"));
        logStudentInProblemRepository.save(new LogStudentInProblem(
                new LogStudentInProblemID(contestId, studentId, lastProblemNo + 1),
                LocalDateTime.now()
        ));
        return new ProblemDto(problemDao.getId(),
                problemDao.getImageURL(), problemDao.getGrade(), problemDao.getProblemType(),
                problemDao.getContents().stream()
                        .map(content -> new ContentDto(content.getContentCompositeId().getProblemId(),
                                content.getContentCompositeId().getContentId(), content.getPreScript(),
                                content.getQuestion(), content.getAnswer(),
                                content.getPostScript())).toList());
    }

}
