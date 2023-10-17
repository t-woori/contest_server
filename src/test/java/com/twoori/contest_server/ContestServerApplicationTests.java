package com.twoori.contest_server;

import com.twoori.contest_server.global.config.TestRedisContainerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(TestRedisContainerConfig.class)
@ActiveProfiles("test")
@SpringBootTest
class ContestServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
