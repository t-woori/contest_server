package com.twoori.contest_server.global;

import org.junit.ClassRule;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Duration;


@TestConfiguration
public class TestContainerConfig {
    @ClassRule
    public static DockerComposeContainer environment =
            new DockerComposeContainer(new File("src/test/resources/compose-test.yml"))
                    .withExposedService("redis", TestRedisConfig.REDIS_PORT,
                            Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
                    .withExposedService("rdb", 3306);
}
