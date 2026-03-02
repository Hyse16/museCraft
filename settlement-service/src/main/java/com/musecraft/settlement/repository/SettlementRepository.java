package com.musecraft.settlement.repository;

import com.musecraft.settlement.domain.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    List<Settlement> findByCreatorIdOrderBySettlementMonthDesc(Long creatorId);

    boolean existsByCreatorIdAndSettlementMonth(Long creatorId, LocalDate month);

    @Query("SELECT SUM(s.creatorAmount) FROM Settlement s WHERE s.creatorId = :creatorId")
    Long sumCreatorAmountByCreatorId(@Param("creatorId") Long creatorId);
}
