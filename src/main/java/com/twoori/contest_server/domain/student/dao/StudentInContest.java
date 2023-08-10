package com.twoori.contest_server.domain.student.dao;

import com.twoori.contest_server.domain.contest.dao.Contest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE student_in_contest SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@Entity
@Table(name = "student_in_contest")
public class StudentInContest {

    @EmbeddedId
    private StudentInContestId id;

    @MapsId("contest_id")
    @ManyToOne(optional = false)
    private Contest contest;

    @MapsId("student_id")
    @ManyToOne(optional = false)
    private Student student;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
