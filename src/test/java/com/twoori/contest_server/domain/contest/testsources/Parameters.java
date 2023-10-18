package com.twoori.contest_server.domain.contest.testsources;

import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class Parameters {
    /**
     * 대회 검색 개수
     *
     * @return 등록된 대회 수, 총 대회 수
     */
    public static Stream<Arguments> argumentsForSearchContest() {
        return Stream.of(
                Arguments.of(0, 100),
                Arguments.of(1, 100),
                Arguments.of(10, 100),
                Arguments.of(100, 100)
        );
    }

    /**
     * 취소 성공
     * @return 대회 시작 시간, 대회 취소 요청시간
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
     * 취소 실패 케이스
     * @return 대회 시작 시간, 대회 취소 요청시간
     */
    public static Stream<Arguments> argumentsForNotCancelTimeAndStartTime() {
        LocalDateTime now = LocalDateTime.of(2023, 2, 2, 14, 0, 0);
        return Stream.of(
                Arguments.of(now, now.minusDays(1)),
                Arguments.of(now, now.minusMinutes(1)),
                Arguments.of(now, now.toLocalDate().atStartOfDay())
        );
    }

    /**
     * 인수는 1개이나 타입이 다르고 여러 곳에서 재사용하기에 별도의 메소드로 작성
     * @return 잘못된 형식
     */

    public static Stream<Arguments> argumentsForWrongContestIds() {
        return Stream.of(
                Arguments.of("asdfasdf"),
                Arguments.of("12L"),
                Arguments.of(1234)
        );
    }
}
