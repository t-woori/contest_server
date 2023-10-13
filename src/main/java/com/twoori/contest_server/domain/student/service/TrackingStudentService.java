package com.twoori.contest_server.domain.student.service;

import com.twoori.contest_server.domain.problem.dto.ProblemIdDto;
import com.twoori.contest_server.domain.problem.dto.UpdateProblemCountDto;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TrackingStudentService {

    private static final String STUDENT_COUNT_KEY = "student_count";
    private final RedisTemplate<String, Long> totalStatusRedisTemplate;
    private final RedisTemplate<String, String> studentRedisTemplate;

    public TrackingStudentService(RedisTemplate<String, Long> totalStatusRedisTemplate, RedisTemplate<String, String> studentRedisTemplate) {
        this.totalStatusRedisTemplate = totalStatusRedisTemplate;
        this.studentRedisTemplate = studentRedisTemplate;
        studentRedisTemplate.expire(STUDENT_COUNT_KEY, Duration.ofHours(1));
    }

    @Async
    public void updateProblemCountAboutStudent(UpdateProblemCountDto newDto) {
        String nullableProblemIdRawValue = studentRedisTemplate.opsForValue().get(newDto.getStudentRedisTemplateKey());
        if (nullableProblemIdRawValue == null) {
            initStudentStatus(newDto);
            return;
        }
        String[] splitValue = nullableProblemIdRawValue.split("_");
        long problemId = Long.parseLong(splitValue[0]);
        long contentId = Long.parseLong(splitValue[1]);
        if (newDto.problemIdDto().problemId() == problemId && newDto.problemIdDto().contentId() == contentId) {
            return;
        }
        UpdateProblemCountDto currentDto = new UpdateProblemCountDto(
                newDto.studentInContestIdDto(),
                new ProblemIdDto(problemId, contentId));
        decreaseStudentCountOnce(currentDto.getTotalStatusRedisTemplateKey());
        increaseStudentCountOnce(newDto.getTotalStatusRedisTemplateKey());
        studentRedisTemplate.opsForValue().set(newDto.getStudentRedisTemplateKey(), newDto.getTotalStatusRedisTemplateKey());
    }

    private void initStudentStatus(UpdateProblemCountDto newDto) {
        increaseStudentCountOnce(newDto.getTotalStatusRedisTemplateKey());
        studentRedisTemplate.expire(newDto.getTotalStatusRedisTemplateKey(), Duration.ofHours(1));
        studentRedisTemplate.opsForValue().set(newDto.getStudentRedisTemplateKey(), newDto.getTotalStatusRedisTemplateKey());
    }

    private void increaseStudentCountOnce(String problemAndContentHashKey) {
        HashOperations<String, String, Long> hashOperations = totalStatusRedisTemplate.opsForHash();
        hashOperations.increment(STUDENT_COUNT_KEY, problemAndContentHashKey, 1L);
    }

    private void decreaseStudentCountOnce(String problemAndContentHashKey) {
        HashOperations<String, String, Long> hashOperations = totalStatusRedisTemplate.opsForHash();
        hashOperations.increment(STUDENT_COUNT_KEY, problemAndContentHashKey, -1L);
    }
//
}
