package com.example.unifiedsecuritymaster.repository;

import com.example.unifiedsecuritymaster.model.CommodityWatchList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommodityWatchListRepository extends JpaRepository<CommodityWatchList, Integer> {
    Optional<CommodityWatchList> findBySymbol(String symbol);
}
