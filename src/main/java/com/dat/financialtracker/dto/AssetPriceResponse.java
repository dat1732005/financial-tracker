package com.dat.financialtracker.dto;

import com.dat.financialtracker.entity.AssetPrice;

import java.math.BigDecimal;
import java.time.Instant;

// Quyet dinh thiet ke:
// 1. Dung Java record cho DTO vi tinh bat bien (immutability), cu phap ngan gon, ho tro mac dinh boi Jackson trong Java 21.
// 2. Dung Instant de dai dien cho thoi gian UTC chuan hoa, tranh lech mui gio giua client va server.
// 3. Dung BigDecimal cho cac muc gia de dam bao do chinh xac tuyet doi trong tinh toan tai chinh, tranh sai so dau phay dong (floating point).
public record AssetPriceResponse(
        Long id,
        String assetCode,
        Instant priceTime,
        BigDecimal openPrice,
        BigDecimal closePrice,
        BigDecimal highPrice,
        BigDecimal lowPrice
) {

    // Factory method giup chuyen doi tu Entity sang DTO mot cach tap trung va an toan (tranh NullPointerException neu asset bi null)
    public static AssetPriceResponse fromEntity(AssetPrice entity) {
        return new AssetPriceResponse(
                entity.getId(),
                entity.getAsset() != null ? entity.getAsset().getCode() : null,
                entity.getPriceTime(),
                entity.getOpenPrice(),
                entity.getClosePrice(),
                entity.getHighPrice(),
                entity.getLowPrice()
        );
    }
}
