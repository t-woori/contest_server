package com.twoori.contest_server.domain.student.service;

import com.twoori.contest_server.domain.problem.dto.ProblemIdDto;
import com.twoori.contest_server.domain.problem.dto.UpdateProblemCountDto;
import com.twoori.contest_server.domain.student.dto.StudentInContestIdDto;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class TrackingStudentService {

    private static final String STUDENT_COUNT_KEY = "student_count";
    private final RedisTemplate<String, Long> totalStatusRedisTemplate;
    private final RedisTemplate<StudentInContestIdDto, ProblemIdDto> studentRedisTemplate;

    public TrackingStudentService(RedisTemplate<String, Long> totalStatusRedisTemplate, RedisTemplate<StudentInContestIdDto, ProblemIdDto> studentRedisTemplate) {
        this.totalStatusRedisTemplate = totalStatusRedisTemplate;
        this.studentRedisTemplate = studentRedisTemplate;
        totalStatusRedisTemplate.expire(STUDENT_COUNT_KEY, Duration.ofHours(1));
    }

    @Async
    public void updateProblemCountAboutStudent(UpdateProblemCountDto afterDto) {
        try {
            ProblemIdDto problemIdDto = studentRedisTemplate.opsForValue().get(afterDto.studentInContestIdDto());
            if (afterDto.problemIdDto().equals(problemIdDto)) {
                return;
            }
            UpdateProblemCountDto beforeDto = new UpdateProblemCountDto(afterDto.studentInContestIdDto(), problemIdDto);
            decreaseStudentCountOnce(beforeDto.problemIdDto());
            increaseStudentCountOnce(afterDto.problemIdDto());
            studentRedisTemplate.opsForValue().setIfPresent(afterDto.studentInContestIdDto(), afterDto.problemIdDto());
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("bytes is null")) {
                initStudentStatus(new UpdateProblemCountDto(
                        afterDto.studentInContestIdDto(),
                        new ProblemIdDto(0L, 0L)));
                return;
            }
            throw e;
        }
    }

    private void initStudentStatus(UpdateProblemCountDto dto) {
        increaseStudentCountOnce(dto.problemIdDto());
        studentRedisTemplate.expire(dto.studentInContestIdDto(), Duration.ofHours(1));
        studentRedisTemplate.opsForValue().setIfAbsent(dto.studentInContestIdDto(), dto.problemIdDto());
    }

    private void increaseStudentCountOnce(ProblemIdDto problemIdDto) {
        HashOperations<String, ProblemIdDto, Long> hashOperations = totalStatusRedisTemplate.opsForHash();
        hashOperations.increment(STUDENT_COUNT_KEY, problemIdDto, 1L);
    }

    public void quitContest(StudentInContestIdDto dto) {
        ProblemIdDto problemIdDto = studentRedisTemplate.opsForValue().get(dto);
        decreaseStudentCountOnce(problemIdDto);
        studentRedisTemplate.delete(dto);
    }

    private void decreaseStudentCountOnce(ProblemIdDto problemIdDto) {
        HashOperations<String, ProblemIdDto, Long> hashOperations = totalStatusRedisTemplate.opsForHash();
        Long count = hashOperations.get(STUDENT_COUNT_KEY, problemIdDto);
        if (count != null && count == 0) {
            return;
        }
        hashOperations.increment(STUDENT_COUNT_KEY, problemIdDto, -1L);
    }

    public ProblemIdDto getStudentStatusInContest(StudentInContestIdDto dto) {
        try {
            return studentRedisTemplate.opsForValue().get(dto);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("bytes is null")) {
                return new ProblemIdDto(0L, 0L);
            }
            throw e;
        }
    }

    public List<Long> getTotalStatus() {
        HashOperations<String, ProblemIdDto, Long> hashOperations = totalStatusRedisTemplate.opsForHash();
        List<ProblemIdDto> problemIdDtos = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            problemIdDtos.add(new ProblemIdDto(i, 0L));
        }
        List<Long> result = new ArrayList<>(hashOperations.multiGet(STUDENT_COUNT_KEY, problemIdDtos));
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i) == null) {
                result.set(i, 0L);
            }
        }
        return result;
    }
}
