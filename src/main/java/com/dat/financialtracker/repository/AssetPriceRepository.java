package com.dat.financialtracker.repository;

import com.dat.financialtracker.entity.AssetPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface AssetPriceRepository extends JpaRepository<AssetPrice, Long> {

    // Quyet dinh thiet ke: Tach thanh cac method rieng biet thay vi dung 1 query co "OR :param IS NULL"
    // giup Postgres de dang su dung index composite tren (asset_id, price_time), tranh table scan.
    Page<AssetPrice> findByAssetCodeAndPriceTimeBetween(
            String code,
            Instant from,
            Instant to,
            Pageable pageable
    );

    Page<AssetPrice> findByAssetCodeAndPriceTimeGreaterThanEqual(
            String code,
            Instant from,
            Pageable pageable
    );

    Page<AssetPrice> findByAssetCodeAndPriceTimeLessThanEqual(
            String code,
            Instant to,
            Pageable pageable
    );

    Page<AssetPrice> findByAssetCode(
            String code,
            Pageable pageable
    );

    // Lay ban ghi gia moi nhat cua tai san: sinh cau lenh SQL sap xep giam dan theo price_time va LIMIT 1
    Optional<AssetPrice> findFirstByAssetCodeOrderByPriceTimeDesc(String code);
}
