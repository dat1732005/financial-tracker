package com.dat.financialtracker.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
    name = "asset_price",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_asset_price_asset_id_price_time",
        columnNames = {"asset_id", "price_time"}
    )
)
public class AssetPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "price_time", nullable = false)
    private Instant priceTime;

    @Column(name = "open_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal openPrice;

    @Column(name = "close_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal closePrice;

    @Column(name = "high_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal highPrice;

    @Column(name = "low_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal lowPrice;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }

    public Instant getPriceTime() { return priceTime; }
    public void setPriceTime(Instant priceTime) { this.priceTime = priceTime; }

    public BigDecimal getOpenPrice() { return openPrice; }
    public void setOpenPrice(BigDecimal openPrice) { this.openPrice = openPrice; }

    public BigDecimal getClosePrice() { return closePrice; }
    public void setClosePrice(BigDecimal closePrice) { this.closePrice = closePrice; }

    public BigDecimal getHighPrice() { return highPrice; }
    public void setHighPrice(BigDecimal highPrice) { this.highPrice = highPrice; }

    public BigDecimal getLowPrice() { return lowPrice; }
    public void setLowPrice(BigDecimal lowPrice) { this.lowPrice = lowPrice; }
}
