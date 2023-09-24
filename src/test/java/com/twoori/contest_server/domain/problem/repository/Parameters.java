package com.twoori.contest_server.domain.problem.repository;

import org.junit.jupiter.params.provider.Arguments;

import java.util.UUID;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Parameters {

    private static final int START_PROBLEM_ID = 0;
    private static final int END_PROBLEM_ID = 9;

    public static Stream<Arguments> parametersOfExistsProblemId() {
        UUID contestId = UUID.fromString("53a703-531f-964b-3984-f92270421862");
        return LongStream.range(START_PROBLEM_ID, END_PROBLEM_ID).mapToObj(id -> Arguments.of(contestId, id));
    }

    public static Stream<Arguments> parametersOfNotExistsProblemId() {
        UUID contestIdAboutMappedProblem = UUID.fromString("53a703-531f-964b-3984-f92270421862");
        return Stream.of(
                Arguments.of(contestIdAboutMappedProblem, END_PROBLEM_ID + 1),
                Arguments.of(contestIdAboutMappedProblem, START_PROBLEM_ID - 1),
                Arguments.of(contestIdAboutMappedProblem, Long.MIN_VALUE),
                Arguments.of(contestIdAboutMappedProblem, Long.MAX_VALUE),
                Arguments.of(contestIdAboutMappedProblem, null),
                Arguments.of(null, START_PROBLEM_ID),
                Arguments.of(null, null),
                Arguments.of(UUID.randomUUID(), null),
                Arguments.of(UUID.randomUUID(), START_PROBLEM_ID),
                Arguments.of(UUID.randomUUID(), Long.MIN_VALUE),
                Arguments.of(UUID.randomUUID(), Long.MAX_VALUE)
        );
    }
}
