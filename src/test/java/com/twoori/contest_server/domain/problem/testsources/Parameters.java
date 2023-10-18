package com.twoori.contest_server.domain.problem.testsources;

import org.junit.jupiter.params.provider.Arguments;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Parameters {

    private static final long START_PROBLEM_ID = 0;
    private static final long END_PROBLEM_ID = 9;

    /**
     * 존재하는 문제
     *
     * @return 문제id, 문제 시퀀스
     */
    public static Stream<Arguments> argumentsOfExistsProblemId() {
        UUID contestId = UUID.fromString("53a70353-1f96-4b39-84f9-22704218627f");
        return LongStream.range(START_PROBLEM_ID, END_PROBLEM_ID).mapToObj(id -> Arguments.of(contestId, id));
    }

    /**
     *
     * @return 대회 id, 대회에 속하지 않은 문제 시퀀스 id
     */
    public static Stream<Arguments> argumentsOfNotExistsProblemId() {
        UUID contestIdAboutMappedProblem = UUID.fromString("53a70353-1f96-4b39-84f9-22704218627f");
        return Stream.of(
                Arguments.of(contestIdAboutMappedProblem, END_PROBLEM_ID + 1),
                Arguments.of(contestIdAboutMappedProblem, START_PROBLEM_ID - 1),
                Arguments.of(contestIdAboutMappedProblem, Long.MIN_VALUE),
                Arguments.of(contestIdAboutMappedProblem, Long.MAX_VALUE),
                Arguments.of(UUID.randomUUID(), START_PROBLEM_ID),
                Arguments.of(UUID.randomUUID(), Long.MIN_VALUE),
                Arguments.of(UUID.randomUUID(), Long.MAX_VALUE)
        );
    }

    /**
     * 문제 아이디가 유효하지 않은 경우
     * @return 대회 id, 문제 id
     */
    public static Stream<Arguments> argumentsOfInvalidProblemId() {
        return Stream.of(
                Arguments.of("aaaa", "1L2B"),
                Arguments.of("asdf", "!@#$"),
                Arguments.of(UUID.randomUUID(), "1L2B"),
                Arguments.of(UUID.randomUUID(), "!@#$"),
                Arguments.of("aaaa", 1L),
                Arguments.of("asdf", 1L)
        );
    }

    /**
     * 문제 점수 평균 테스트
     * 유효자리를 5자리까지로 보기에 반환되는 문제 자릿수를 5자리로 맞춤
     * @return 각 문제 점수, 문제 평균
     */
    public static Stream<Arguments> argumentsOfMaxScoreAboutProblems() {
        List<Arguments> arguments = new LinkedList<>();
        List<Double> scores = List.of(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);
        for (int i = 0; i < scores.size(); i++) {
            List<Double> subScores = new LinkedList<>();
            for (int j = 0; j <= i; j++) {
                subScores.add(scores.get(j));
            }
            double average = Math.floor(subScores.stream()
                    .mapToDouble(Double::doubleValue).average().orElseThrow() * 10000) / 10000;
            arguments.add(Arguments.of(subScores, average));
        }
        return arguments.stream();
    }

    /**
     * 문제 개수 10개에 대한 각각 인원수
     *
     * @return List<Integer>
     */
    public static Stream<Arguments> argumentsForTotalStatus() {
        return Stream.of(
                Arguments.of(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L)),
                Arguments.of(List.of(1L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L)),
                Arguments.of(List.of(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 100L))
        );
    }
}
