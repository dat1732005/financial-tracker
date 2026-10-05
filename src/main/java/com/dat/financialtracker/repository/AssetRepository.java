package com.dat.financialtracker.repository;

import com.dat.financialtracker.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {
}
