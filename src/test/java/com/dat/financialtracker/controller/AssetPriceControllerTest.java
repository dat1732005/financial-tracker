package com.dat.financialtracker.controller;

import com.dat.financialtracker.dto.AssetPriceResponse;
import com.dat.financialtracker.exception.GlobalExceptionHandler;
import com.dat.financialtracker.exception.ResourceNotFoundException;
import com.dat.financialtracker.service.AssetPriceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AssetPriceController.class)
@Import(GlobalExceptionHandler.class)
class AssetPriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssetPriceService assetPriceService;

    @Test
    @DisplayName("GET /api/assets/{code}/prices trả về 200 OK với danh sách phân trang")
    void getHistoricalPrices_success() throws Exception {
        AssetPriceResponse response = new AssetPriceResponse(
                1L,
                "VNM",
                Instant.parse("2026-10-07T08:00:00Z"),
                new BigDecimal("75000.0000"),
                new BigDecimal("75500.0000"),
                new BigDecimal("76000.0000"),
                new BigDecimal("74800.0000")
        );
        PageImpl<AssetPriceResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);

        when(assetPriceService.getHistoricalPrices(eq("VNM"), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/assets/VNM/prices")
                        .param("from", "2026-10-01T00:00:00Z")
                        .param("to", "2026-10-07T23:59:59Z")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].assetCode").value("VNM"))
                .andExpect(jsonPath("$.content[0].closePrice").value(75500.0000))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/assets/{code}/prices trả về 404 khi mã tài sản không tồn tại")
    void getHistoricalPrices_notFound() throws Exception {
        when(assetPriceService.getHistoricalPrices(eq("UNKNOWN"), any(), any(), any()))
                .thenThrow(new ResourceNotFoundException("Không tìm thấy tài sản với mã: UNKNOWN"));

        mockMvc.perform(get("/api/assets/UNKNOWN/prices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Không tìm thấy tài sản với mã: UNKNOWN"));
    }

    @Test
    @DisplayName("GET /api/assets/{code}/prices trả về 400 khi 'from' lớn hơn 'to'")
    void getHistoricalPrices_badRequest() throws Exception {
        when(assetPriceService.getHistoricalPrices(eq("VNM"), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Thời điểm 'from' không được lớn hơn 'to'"));

        mockMvc.perform(get("/api/assets/VNM/prices")
                        .param("from", "2026-10-10T00:00:00Z")
                        .param("to", "2026-10-01T00:00:00Z")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Thời điểm 'from' không được lớn hơn 'to'"));
    }

    @Test
    @DisplayName("GET /api/assets/{code}/prices/latest trả về 200 OK với giá mới nhất")
    void getLatestPrice_success() throws Exception {
        AssetPriceResponse response = new AssetPriceResponse(
                1L,
                "VNM",
                Instant.parse("2026-10-07T08:00:00Z"),
                new BigDecimal("75000.0000"),
                new BigDecimal("75500.0000"),
                new BigDecimal("76000.0000"),
                new BigDecimal("74800.0000")
        );

        when(assetPriceService.getLatestPrice("VNM")).thenReturn(response);

        mockMvc.perform(get("/api/assets/VNM/prices/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetCode").value("VNM"))
                .andExpect(jsonPath("$.closePrice").value(75500.0000));
    }

    @Test
    @DisplayName("GET /api/assets/{code}/prices/latest trả về 404 khi mã không tồn tại")
    void getLatestPrice_notFound() throws Exception {
        when(assetPriceService.getLatestPrice("UNKNOWN"))
                .thenThrow(new ResourceNotFoundException("Không tìm thấy tài sản với mã: UNKNOWN"));

        mockMvc.perform(get("/api/assets/UNKNOWN/prices/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Không tìm thấy tài sản với mã: UNKNOWN"));
    }
}
