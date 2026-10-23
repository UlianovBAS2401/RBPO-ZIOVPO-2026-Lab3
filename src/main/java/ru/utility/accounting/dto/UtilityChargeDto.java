package ru.utility.accounting.dto;

import java.time.OffsetDateTime;

public class UtilityChargeDto {

    private Long id;
    private Long premisesId;
    private Long meterId;
    private String period;
    private Double volume;
    private Double amount;
    private String status;
    private OffsetDateTime createdAt;

    public UtilityChargeDto() {
    }

    public UtilityChargeDto(Long id, Long premisesId, Long meterId, String period, Double volume, Double amount, String status, OffsetDateTime createdAt) {
        this.id = id;
        this.premisesId = premisesId;
        this.meterId = meterId;
        this.period = period;
        this.volume = volume;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPremisesId() {
        return premisesId;
    }

    public void setPremisesId(Long premisesId) {
        this.premisesId = premisesId;
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

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

