package ru.utility.accounting.dto;

import java.time.OffsetDateTime;

public class MeterReadingDto {

    private Long id;
    private Long meterId;
    private String period;
    private Double readingValue;
    private String verificationStatus;
    private OffsetDateTime recordedAt;

    public MeterReadingDto() {
    }

    public MeterReadingDto(Long id, Long meterId, String period, Double readingValue, String verificationStatus, OffsetDateTime recordedAt) {
        this.id = id;
        this.meterId = meterId;
        this.period = period;
        this.readingValue = readingValue;
        this.verificationStatus = verificationStatus;
        this.recordedAt = recordedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMeterId() {
        return meterId;
    }

    public void setMeterId(Long meterId) {
        this.meterId = meterId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getReadingValue() {
        return readingValue;
    }

    public void setReadingValue(Double readingValue) {
        this.readingValue = readingValue;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public OffsetDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(OffsetDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}

