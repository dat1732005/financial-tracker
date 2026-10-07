package com.dat.financialtracker.repository;

import com.dat.financialtracker.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    // Kiem tra su ton tai cua ma tai san nhanh chong (tra ve boolean) ma khong can load toan bo entity
    boolean existsByCode(String code);

    Optional<Asset> findByCode(String code);
}
