package com.dat.financialtracker.controller;

import com.dat.financialtracker.dto.AssetPriceResponse;
import com.dat.financialtracker.service.AssetPriceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/assets/{code}/prices")
public class AssetPriceController {

    private final AssetPriceService assetPriceService;

    public AssetPriceController(AssetPriceService assetPriceService) {
        this.assetPriceService = assetPriceService;
    }

    // Endpoint 1: GET /api/assets/{code}/prices?from=...&to=...&page=...&size=...
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME): Chuyen doi chuoi ISO-8601 tu query param thanh Instant UTC mot cach an toan.
    // @PageableDefault: Dat mac dinh sap xep theo priceTime giam dan (du lieu moi nhat len dau) neu client khong chi dinh sort.
    @GetMapping
    public Page<AssetPriceResponse> getHistoricalPrices(
            @PathVariable("code") String code,
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @PageableDefault(sort = "priceTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return assetPriceService.getHistoricalPrices(code, from, to, pageable);
    }

    // Endpoint 2: GET /api/assets/{code}/prices/latest
    // Tra ve muc gia moi nhat cua tai san theo ma code; neu code khong ton tai hoac chua co gia se tra ve HTTP 404 ro rang.
    @GetMapping("/latest")
    public AssetPriceResponse getLatestPrice(@PathVariable("code") String code) {
        return assetPriceService.getLatestPrice(code);
    }
}
