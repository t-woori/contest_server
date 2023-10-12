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

    public static Stream<Arguments> parametersOfExistsProblemId() {
        UUID contestId = UUID.fromString("53a70353-1f96-4b39-84f9-22704218627f");
        return LongStream.range(START_PROBLEM_ID, END_PROBLEM_ID).mapToObj(id -> Arguments.of(contestId, id));
    }

    public static Stream<Arguments> parametersOfNotExistsProblemId() {
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

    public static Stream<Arguments> parametersOfInvalidProblemId() {
        return Stream.of(
                Arguments.of("aaaa", "1L2B"),
                Arguments.of("asdf", "!@#$"),
                Arguments.of(UUID.randomUUID(), "1L2B"),
                Arguments.of(UUID.randomUUID(), "!@#$"),
                Arguments.of("aaaa", 1L),
                Arguments.of("asdf", 1L)
        );
    }

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
}
