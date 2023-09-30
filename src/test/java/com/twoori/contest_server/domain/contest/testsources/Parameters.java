package com.twoori.contest_server.domain.contest.testsources;

import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class Parameters {
    /**
     * @return contestTime, cancelTime
     */
    public static Stream<Arguments> argumentsForCancelTimeAndStartTime() {
        LocalDateTime now = LocalDateTime.of(2023, 2, 2, 14, 0, 0);
        return Stream.of(
                Arguments.of(now, now.plusDays(1)),
                Arguments.of(now, now.plusDays(2)),
                Arguments.of(now, now.plusDays(3)),
                Arguments.of(now, now.plusDays(4))
        );
    }

    /**
     * @return contestTime, cancelTime
     */
    public static Stream<Arguments> argumentsForNotCancelTimeAndStartTime() {
        LocalDateTime now = LocalDateTime.of(2023, 2, 2, 14, 0, 0);
        return Stream.of(
                Arguments.of(now, now.minusDays(1)),
                Arguments.of(now, now.minusMinutes(1)),
                Arguments.of(now, now.toLocalDate().atStartOfDay())
        );
    }
}
