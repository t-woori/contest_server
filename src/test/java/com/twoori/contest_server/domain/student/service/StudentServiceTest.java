package com.twoori.contest_server.domain.student.service;

import com.twoori.contest_server.domain.student.dao.StudentInContest;
import com.twoori.contest_server.domain.student.dao.StudentInContestID;
import com.twoori.contest_server.domain.student.dto.ResultContestDto;
import com.twoori.contest_server.domain.student.repository.StudentInContestRepository;
import com.twoori.contest_server.domain.student.repository.StudentRepository;
import com.twoori.contest_server.global.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith({MockitoExtension.class})
class StudentServiceTest {

    @InjectMocks
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentInContestRepository studentInContestRepository;


    @DisplayName("학생의 대회 점수 조회|Success|학생 등수와 점수 조회")
    @Test
    void givenStudentAndContestId_whenGetScoreAndRank_thenSuccess() {
        // given
        UUID studentId = UUID.randomUUID();
        UUID contestId = UUID.randomUUID();
        StudentInContestID id = new StudentInContestID(studentId, contestId);
        double studentScore = 100.0;
        long studentRank = 1L;
        given(studentInContestRepository.findById(id))
                .willReturn(Optional.of(StudentInContest.builder()
                        .id(id)
                        .studentScore(studentScore)
                        .studentRank(studentRank)
                        .build()));
        // when
        ResultContestDto resultContestDto = studentService.getScoreAndRank(contestId, studentId);

        // then
        assertThat(resultContestDto)
                .isEqualTo(new ResultContestDto(studentScore, studentRank));
    }

    @DisplayName("학생의 대회 점수 조회|Fail|존재하지 않는 학생 혹은 대회 조회")
    @Test
    void givenStudentAndContestID_whenGetScoreAndRank_thenThrowNotFoundException() {
        // given
        UUID studentId = UUID.randomUUID();
        UUID contestId = UUID.randomUUID();
        StudentInContestID id = new StudentInContestID(studentId, contestId);
        given(studentInContestRepository.findById(id))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studentService.getScoreAndRank(contestId, studentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found student in contest");
    }
}