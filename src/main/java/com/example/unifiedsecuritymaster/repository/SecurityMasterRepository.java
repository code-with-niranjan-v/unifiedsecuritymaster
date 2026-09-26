package com.example.unifiedsecuritymaster.repository;

import com.example.unifiedsecuritymaster.model.SecurityMaster;
import com.example.unifiedsecuritymaster.model.enums.SecurityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SecurityMasterRepository extends JpaRepository<SecurityMaster, Long> {

    Optional<SecurityMaster> findBySymbolAndExchangeCode(String symbol, String exchangeCode);

    Optional<SecurityMaster> findByIsinAndExchangeCode(String isin, String exchangeCode);

    List<SecurityMaster> findByIsin(String isin);

    List<SecurityMaster> findByGicsSectorIgnoreCase(String sector);

    List<SecurityMaster> findByGicsSectorIgnoreCaseAndGicsIndustryIgnoreCase(
            String sector, String industry);

    List<SecurityMaster> findBySecurityType(SecurityType type);

    @Query("SELECT s.securityType, COUNT(s) FROM SecurityMaster s GROUP BY s.securityType")
    List<Object[]> countByType();
}
