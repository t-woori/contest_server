package com.twoori.contest_server.domain.contest.repository;

import com.twoori.contest_server.domain.contest.dto.ContestDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DataJpaTest
class ContestRepositoryTest {

    @Autowired
    private ContestRepository repository;

    @DisplayName("신청한 대회 중 시작하지 않은 대회 조회|Success|2건의 대회가 존재")
    @Test
    void givenStudentIdWhenGetRegisteredContestsInFromToThenListSizeOf2() {
        UUID studentId = UUID.fromString("d7762394-592c-4e33-8d71-06fc5a94abfb");
        LocalDateTime from = LocalDateTime.now().minusMinutes(1);
        LocalDateTime to = from.plusMonths(3);
        List<ContestDto> actual = repository.getRegisteredContestsInFromTo(studentId, from, to);
        assertThat(actual)
                .isNotNull()
                .hasSize(2)
                .extracting("id")
                // contain all element
                .containsExactlyInAnyOrderElementsOf(List.of(
                        UUID.fromString("992033a0-11c9-45b0-a643-01a2c706f118"),
                        UUID.fromString("53a70353-1f96-4b39-84f9-22704218627f")));

    }
}