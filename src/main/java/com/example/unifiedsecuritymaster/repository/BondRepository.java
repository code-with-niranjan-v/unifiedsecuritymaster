package com.example.unifiedsecuritymaster.repository;

import com.example.unifiedsecuritymaster.model.Bond;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BondRepository extends JpaRepository<Bond, Integer> {

    List<Bond> findByExchange(String exchange);

    public Bond findByIsin(String isin);

    boolean existsByIsin(String isin);
}
