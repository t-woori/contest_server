package com.twoori.contest_server.global.config;

import com.twoori.contest_server.domain.problem.dto.ProblemDto;
import com.twoori.contest_server.domain.problem.dto.ProblemIdDto;
import com.twoori.contest_server.domain.student.dto.StudentInContestIdDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.*;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@EnableRedisRepositories
@Configuration
public class RedisConfig {

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisConfig(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, ProblemDto> redisTemplate() {
        RedisTemplate<String, ProblemDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<StudentInContestIdDto, ProblemIdDto> studentRedisTemplate() {
        RedisTemplate<StudentInContestIdDto, ProblemIdDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StudentInContestDtoSerializer());
        redisTemplate.setValueSerializer(new ProblemIdDtoSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, Long> totalStatusRedisTemplate() {
        RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new ProblemIdDtoSerializer());
        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));
        return redisTemplate;
    }

    static class StudentInContestDtoSerializer implements RedisSerializer<StudentInContestIdDto> {
        @Override
        public byte[] serialize(StudentInContestIdDto studentInContestIdDto) throws SerializationException {
            if (Objects.isNull(studentInContestIdDto)) {
                throw new IllegalArgumentException("studentInContestIdDto is null");
            }
            String value = studentInContestIdDto.contestId() + "_" + studentInContestIdDto.studentId();
            return value.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public StudentInContestIdDto deserialize(byte[] bytes) throws SerializationException {
            String value = new String(bytes, StandardCharsets.UTF_8);
            String[] splitValue = value.split("_");
            if (splitValue.length != 2) {
                throw new IllegalArgumentException("studentInContestIdDto is invalid: " + value);
            }
            return new StudentInContestIdDto(UUID.fromString(splitValue[0]), UUID.fromString(splitValue[1]));
        }
    }

    static class ProblemIdDtoSerializer implements RedisSerializer<ProblemIdDto> {

        @Override
        public byte[] serialize(ProblemIdDto problemIdDto) throws SerializationException {
            if (Objects.isNull(problemIdDto)) {
                throw new IllegalArgumentException("problemIdDto is null");
            }
            String value = problemIdDto.problemId() + "_" + problemIdDto.contentId();
            return value.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public ProblemIdDto deserialize(byte[] bytes) throws SerializationException {
            if (Objects.isNull(bytes)) {
                throw new IllegalArgumentException("bytes is null");
            }
            String value = new String(bytes, StandardCharsets.UTF_8);
            String[] splitValue = value.split("_");
            if (splitValue.length != 2) {
                throw new IllegalArgumentException("problemIdDto is invalid: " + value);
            }
            return new ProblemIdDto(Long.parseLong(splitValue[0]), Long.parseLong(splitValue[1]));
        }
    }
}
