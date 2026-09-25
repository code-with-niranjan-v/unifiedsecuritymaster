package com.example.unifiedsecuritymaster.repository;

import com.example.unifiedsecuritymaster.model.CommoditySpotData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CommoditySpotDataRepository extends JpaRepository<CommoditySpotData, Long> {


    @Query("SELECT MAX(c.spotDate) FROM CommoditySpotData c " +
            "WHERE c.symbol = :symbol AND c.isFinal = true")
    Optional<LocalDate> findLatestFinalDate(@Param("symbol") String symbol);

    @Query("SELECT c.symbol, COUNT(c), MIN(c.spotDate), MAX(c.spotDate) " +
            "FROM CommoditySpotData c GROUP BY c.symbol ORDER BY c.symbol")
    List<Object[]> summariseBySymbol();
}
