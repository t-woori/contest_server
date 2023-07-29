package com.twoori.contest_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@EntityScan
@EnableJpaAuditing
@SpringBootApplication
public class ContestServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContestServerApplication.class, args);
    }

}
