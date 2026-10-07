package com.dat.financialtracker.service;

import com.dat.financialtracker.dto.AssetPriceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface AssetPriceService {

    // Lay lich su gia cua mot tai san theo khoang thoi gian (from/to tuy chon) kem phan trang
    Page<AssetPriceResponse> getHistoricalPrices(String code, Instant from, Instant to, Pageable pageable);

    // Lay muc gia ghi nhan moi nhat cua mot tai san
    AssetPriceResponse getLatestPrice(String code);
}
