package com.musecraft.brand.repository;

import com.musecraft.brand.domain.AiGenerationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AiGenerationLogRepository extends JpaRepository<AiGenerationLog, Long> {

    @Query("SELECT COUNT(l) FROM AiGenerationLog l WHERE l.userId = :userId AND l.createdAt >= :since")
    long countByUserIdAndCreatedAtAfter(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
