package com.example.unifiedsecuritymaster.repository;

import com.example.unifiedsecuritymaster.model.MutualFundNav;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MutualFundNavRepository extends JpaRepository<MutualFundNav, Long> {


    @Query("SELECT MAX(n.navDate) FROM MutualFundNav n WHERE n.isin = :isin")
    Optional<LocalDate> findLatestNavDate(@Param("isin") String isin);

    @Query("SELECT n.isin, COUNT(n), MIN(n.navDate), MAX(n.navDate) " +
            "FROM MutualFundNav n GROUP BY n.isin ORDER BY n.isin")
    List<Object[]> summariseByIsin();
}
