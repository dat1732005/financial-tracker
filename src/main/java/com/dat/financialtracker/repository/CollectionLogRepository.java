package com.dat.financialtracker.repository;

import com.dat.financialtracker.entity.CollectionLog;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository quan ly truy van va luu tru log cua cac job thu thap du lieu
public interface CollectionLogRepository extends JpaRepository<CollectionLog, Long> {
}
