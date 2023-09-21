package com.twoori.contest_server.domain.contest.repository;

import com.twoori.contest_server.domain.contest.dao.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContestRepository extends JpaRepository<Contest, UUID>, ContestRepositoryCustom {

}