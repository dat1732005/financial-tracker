package com.dat.financialtracker.repository;

import com.dat.financialtracker.entity.AssetPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface AssetPriceRepository extends JpaRepository<AssetPrice, Long> {

    Page<AssetPrice> findByAssetCodeAndPriceTimeBetween(
            String code,
            Instant from,
            Instant to,
            Pageable pageable
    );
}
