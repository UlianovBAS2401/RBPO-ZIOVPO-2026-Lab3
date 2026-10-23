package ru.utility.accounting.dto;

import java.time.OffsetDateTime;

public class PremisesDto {

    private Long id;
    private String address;
    private Double area;
    private Long userId;
    private OffsetDateTime createdAt;

    public PremisesDto() {
    }

    public PremisesDto(Long id, String address, Double area, Long userId, OffsetDateTime createdAt) {
        this.id = id;
        this.address = address;
        this.area = area;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

