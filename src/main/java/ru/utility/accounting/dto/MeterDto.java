package ru.utility.accounting.dto;

import java.time.OffsetDateTime;

public class MeterDto {

    private Long id;
    private Long premisesId;
    private String resourceType;
    private String serialNumber;
    private Double unitRate;
    private String status;
    private OffsetDateTime createdAt;

    public MeterDto() {
    }

    public MeterDto(Long id, Long premisesId, String resourceType, String serialNumber, Double unitRate, String status, OffsetDateTime createdAt) {
        this.id = id;
        this.premisesId = premisesId;
        this.resourceType = resourceType;
        this.serialNumber = serialNumber;
        this.unitRate = unitRate;
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

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Double getUnitRate() {
        return unitRate;
    }

    public void setUnitRate(Double unitRate) {
        this.unitRate = unitRate;
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

