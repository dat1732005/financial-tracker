package com.dat.financialtracker.service.impl;

import com.dat.financialtracker.dto.AssetPriceResponse;
import com.dat.financialtracker.entity.AssetPrice;
import com.dat.financialtracker.exception.ResourceNotFoundException;
import com.dat.financialtracker.repository.AssetPriceRepository;
import com.dat.financialtracker.repository.AssetRepository;
import com.dat.financialtracker.service.AssetPriceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
// @Transactional(readOnly = true): Toi uu hoa hieu nang cho cac tac vu chi doc (read-only query).
// Hibernate se bo qua co che dirty checking tren entity, giup tiet kiem bo nho va CPU.
@Transactional(readOnly = true)
public class AssetPriceServiceImpl implements AssetPriceService {

    private final AssetPriceRepository assetPriceRepository;
    private final AssetRepository assetRepository;

    public AssetPriceServiceImpl(AssetPriceRepository assetPriceRepository, AssetRepository assetRepository) {
        this.assetPriceRepository = assetPriceRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    public Page<AssetPriceResponse> getHistoricalPrices(String code, Instant from, Instant to, Pageable pageable) {
        // Logic nghiep vu: Kiem tra tai san co ton tai khong truoc khi truy van lich su gia,
        // neu khong ton tai thi bao loi 404 thay vi tra ve danh sach rong (HTTP 200).
        if (!assetRepository.existsByCode(code)) {
            throw new ResourceNotFoundException("Không tìm thấy tài sản với mã: " + code);
        }

        // Kiem tra tinh hop le cua khoang thoi gian from va to
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("Thời điểm 'from' không được lớn hơn 'to'");
        }

        // Quyet dinh thiet ke: Phan nhanh goi cac query method tuong ung voi tung truong hop tham so
        // de dam bao query SQL sinh ra don gian, giup Postgres query planner luon tan dung composite index tren (asset_id, price_time).
        Page<AssetPrice> pricePage;
        if (from != null && to != null) {
            pricePage = assetPriceRepository.findByAssetCodeAndPriceTimeBetween(code, from, to, pageable);
        } else if (from != null) {
            pricePage = assetPriceRepository.findByAssetCodeAndPriceTimeGreaterThanEqual(code, from, pageable);
        } else if (to != null) {
            pricePage = assetPriceRepository.findByAssetCodeAndPriceTimeLessThanEqual(code, to, pageable);
        } else {
            pricePage = assetPriceRepository.findByAssetCode(code, pageable);
        }

        return pricePage.map(AssetPriceResponse::fromEntity);
    }

    @Override
    public AssetPriceResponse getLatestPrice(String code) {
        // Kiem tra tai san co ton tai trong he thong hay khong
        if (!assetRepository.existsByCode(code)) {
            throw new ResourceNotFoundException("Không tìm thấy tài sản với mã: " + code);
        }

        // Lay gia moi nhat, neu chua co ban ghi nao thi tra ve 404 thong bao ro rang
        AssetPrice latestPrice = assetPriceRepository.findFirstByAssetCodeOrderByPriceTimeDesc(code)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dữ liệu giá cho tài sản với mã: " + code));

        return AssetPriceResponse.fromEntity(latestPrice);
    }
}
