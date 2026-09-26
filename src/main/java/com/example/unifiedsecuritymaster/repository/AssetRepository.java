package com.example.unifiedsecuritymaster.repository;

import com.example.unifiedsecuritymaster.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Integer> {

    List<Asset> findByAssetClass(String assetClass);

    Optional<Asset> findFirstByAssetSubclassIgnoreCase(String subclass);
}
