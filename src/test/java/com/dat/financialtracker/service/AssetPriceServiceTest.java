package com.dat.financialtracker.service;

import com.dat.financialtracker.dto.AssetPriceResponse;
import com.dat.financialtracker.entity.Asset;
import com.dat.financialtracker.entity.AssetPrice;
import com.dat.financialtracker.exception.ResourceNotFoundException;
import com.dat.financialtracker.repository.AssetPriceRepository;
import com.dat.financialtracker.repository.AssetRepository;
import com.dat.financialtracker.service.impl.AssetPriceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetPriceServiceTest {

    @Mock
    private AssetPriceRepository assetPriceRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetPriceServiceImpl assetPriceService;

    private Asset sampleAsset;
    private AssetPrice samplePrice;

    @BeforeEach
    void setUp() {
        sampleAsset = new Asset();
        sampleAsset.setId(1L);
        sampleAsset.setCode("VNM");
        sampleAsset.setName("Vinamilk");
        sampleAsset.setType(Asset.AssetType.STOCK);

        samplePrice = new AssetPrice();
        samplePrice.setId(10L);
        samplePrice.setAsset(sampleAsset);
        samplePrice.setPriceTime(Instant.parse("2026-10-07T08:00:00Z"));
        samplePrice.setOpenPrice(new BigDecimal("75000.0000"));
        samplePrice.setClosePrice(new BigDecimal("75500.0000"));
        samplePrice.setHighPrice(new BigDecimal("76000.0000"));
        samplePrice.setLowPrice(new BigDecimal("74800.0000"));
    }

    @Test
    @DisplayName("Ném ResourceNotFoundException nếu mã tài sản không tồn tại khi lấy lịch sử giá")
    void getHistoricalPrices_throwsNotFound_whenAssetCodeDoesNotExist() {
        when(assetRepository.existsByCode("UNKNOWN")).thenReturn(false);

        assertThatThrownBy(() -> assetPriceService.getHistoricalPrices("UNKNOWN", null, null, PageRequest.of(0, 10)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Không tìm thấy tài sản với mã: UNKNOWN");

        verifyNoInteractions(assetPriceRepository);
    }

    @Test
    @DisplayName("Ném IllegalArgumentException nếu 'from' lớn hơn 'to'")
    void getHistoricalPrices_throwsIllegalArgument_whenFromIsAfterTo() {
        when(assetRepository.existsByCode("VNM")).thenReturn(true);
        Instant from = Instant.parse("2026-10-05T00:00:00Z");
        Instant to = Instant.parse("2026-10-01T00:00:00Z");

        assertThatThrownBy(() -> assetPriceService.getHistoricalPrices("VNM", from, to, PageRequest.of(0, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Thời điểm 'from' không được lớn hơn 'to'");

        verifyNoInteractions(assetPriceRepository);
    }

    @Test
    @DisplayName("Lấy lịch sử giá khi có cả 'from' và 'to'")
    void getHistoricalPrices_withBothFromAndTo() {
        when(assetRepository.existsByCode("VNM")).thenReturn(true);
        Instant from = Instant.parse("2026-10-01T00:00:00Z");
        Instant to = Instant.parse("2026-10-05T00:00:00Z");
        Pageable pageable = PageRequest.of(0, 10);
        Page<AssetPrice> page = new PageImpl<>(List.of(samplePrice), pageable, 1);

        when(assetPriceRepository.findByAssetCodeAndPriceTimeBetween("VNM", from, to, pageable))
                .thenReturn(page);

        Page<AssetPriceResponse> result = assetPriceService.getHistoricalPrices("VNM", from, to, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).assetCode()).isEqualTo("VNM");
        assertThat(result.getContent().get(0).closePrice()).isEqualTo(new BigDecimal("75500.0000"));
        verify(assetPriceRepository).findByAssetCodeAndPriceTimeBetween("VNM", from, to, pageable);
    }

    @Test
    @DisplayName("Lấy lịch sử giá khi chỉ có 'from'")
    void getHistoricalPrices_withOnlyFrom() {
        when(assetRepository.existsByCode("VNM")).thenReturn(true);
        Instant from = Instant.parse("2026-10-01T00:00:00Z");
        Pageable pageable = PageRequest.of(0, 10);
        Page<AssetPrice> page = new PageImpl<>(List.of(samplePrice), pageable, 1);

        when(assetPriceRepository.findByAssetCodeAndPriceTimeGreaterThanEqual("VNM", from, pageable))
                .thenReturn(page);

        Page<AssetPriceResponse> result = assetPriceService.getHistoricalPrices("VNM", from, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(assetPriceRepository).findByAssetCodeAndPriceTimeGreaterThanEqual("VNM", from, pageable);
    }

    @Test
    @DisplayName("Lấy lịch sử giá khi chỉ có 'to'")
    void getHistoricalPrices_withOnlyTo() {
        when(assetRepository.existsByCode("VNM")).thenReturn(true);
        Instant to = Instant.parse("2026-10-05T00:00:00Z");
        Pageable pageable = PageRequest.of(0, 10);
        Page<AssetPrice> page = new PageImpl<>(List.of(samplePrice), pageable, 1);

        when(assetPriceRepository.findByAssetCodeAndPriceTimeLessThanEqual("VNM", to, pageable))
                .thenReturn(page);

        Page<AssetPriceResponse> result = assetPriceService.getHistoricalPrices("VNM", null, to, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(assetPriceRepository).findByAssetCodeAndPriceTimeLessThanEqual("VNM", to, pageable);
    }

    @Test
    @DisplayName("Lấy lịch sử giá khi không truyền 'from' và 'to'")
    void getHistoricalPrices_withoutFromAndTo() {
        when(assetRepository.existsByCode("VNM")).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 10);
        Page<AssetPrice> page = new PageImpl<>(List.of(samplePrice), pageable, 1);

        when(assetPriceRepository.findByAssetCode("VNM", pageable))
                .thenReturn(page);

        Page<AssetPriceResponse> result = assetPriceService.getHistoricalPrices("VNM", null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(assetPriceRepository).findByAssetCode("VNM", pageable);
    }

    @Test
    @DisplayName("Ném ResourceNotFoundException nếu mã tài sản không tồn tại khi lấy giá mới nhất")
    void getLatestPrice_throwsNotFound_whenAssetCodeDoesNotExist() {
        when(assetRepository.existsByCode("UNKNOWN")).thenReturn(false);

        assertThatThrownBy(() -> assetPriceService.getLatestPrice("UNKNOWN"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Không tìm thấy tài sản với mã: UNKNOWN");

        verifyNoInteractions(assetPriceRepository);
    }

    @Test
    @DisplayName("Ném ResourceNotFoundException nếu tài sản tồn tại nhưng chưa có dữ liệu giá")
    void getLatestPrice_throwsNotFound_whenNoPriceData() {
        when(assetRepository.existsByCode("VNM")).thenReturn(true);
        when(assetPriceRepository.findFirstByAssetCodeOrderByPriceTimeDesc("VNM")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetPriceService.getLatestPrice("VNM"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Không tìm thấy dữ liệu giá cho tài sản với mã: VNM");
    }

    @Test
    @DisplayName("Lấy giá mới nhất thành công khi có dữ liệu")
    void getLatestPrice_success() {
        when(assetRepository.existsByCode("VNM")).thenReturn(true);
        when(assetPriceRepository.findFirstByAssetCodeOrderByPriceTimeDesc("VNM")).thenReturn(Optional.of(samplePrice));

        AssetPriceResponse response = assetPriceService.getLatestPrice("VNM");

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.assetCode()).isEqualTo("VNM");
        assertThat(response.closePrice()).isEqualTo(new BigDecimal("75500.0000"));
    }
}
