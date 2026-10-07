package com.dat.financialtracker.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "collection_log")
public class CollectionLog {

    // GenerationType.IDENTITY phu hop voi kieu BIGSERIAL trong PostgreSQL, de database tu quan ly sequence tang ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_type", nullable = false, length = 50)
    private String jobType;

    // Quyet dinh thiet ke: Dung Instant luu tru moc thoi gian theo chuan UTC (khop voi TIMESTAMPTZ o DB),
    // tranh lech gio giua server va client khi ghi nhan lich su chay job.
    @Column(name = "run_at", nullable = false)
    private Instant runAt;

    @Column(name = "duration_ms")
    private Long durationMs;

    // Quyet dinh thiet ke: Dung EnumType.STRING de luu ten chuoi 'SUCCESS'/'FAILED' vao DB thay vi so thu tu (ORDINAL),
    // giup tranh loi sai lech du lieu neu sau nay enum duoc bo sung them gia tri moi.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CollectionStatus status;

    // columnDefinition = "TEXT": Cho phep luu tru chi tiet log loi dai ma khong bi gioi han boi do dai VARCHAR
    @Column(columnDefinition = "TEXT")
    private String message;

    public enum CollectionStatus {
        SUCCESS, FAILED
    }

    public CollectionLog() {
    }

    public CollectionLog(String jobType, Instant runAt, Long durationMs, CollectionStatus status, String message) {
        this.jobType = jobType;
        this.runAt = runAt;
        this.durationMs = durationMs;
        this.status = status;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public Instant getRunAt() {
        return runAt;
    }

    public void setRunAt(Instant runAt) {
        this.runAt = runAt;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public CollectionStatus getStatus() {
        return status;
    }

    public void setStatus(CollectionStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
