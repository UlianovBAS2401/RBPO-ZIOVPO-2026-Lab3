package ru.utility.accounting.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.utility.accounting.dto.MeterDto;
import ru.utility.accounting.entity.Meter;
import ru.utility.accounting.service.MeterService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/meters")
public class MeterController {

    private final MeterService meterService;

    public MeterController(MeterService meterService) {
        this.meterService = meterService;
    }

    @GetMapping
    public ResponseEntity<List<MeterDto>> getAllMeters() {
        List<Meter> list = meterService.findAll();
        List<MeterDto> dtoList = new ArrayList<>();
        for (Meter m : list) {
            dtoList.add(toDto(m));
        }
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeterDto> getMeterById(@PathVariable Long id) {
        Meter meter = meterService.findById(id);
        return ResponseEntity.ok(toDto(meter));
    }

    @PostMapping
    public ResponseEntity<MeterDto> createMeter(@RequestBody MeterDto dto) {
        Meter meter = toEntity(dto);
        Meter created = meterService.create(meter, dto.getPremisesId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MeterDto> patchMeter(@PathVariable Long id, @RequestBody MeterDto dto) {
        Meter updated = meterService.patch(id, dto);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeter(@PathVariable Long id) {
        meterService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private MeterDto toDto(Meter meter) {
        if (meter == null) {
            return null;
        }
        Long premisesId = (meter.getPremises() != null) ? meter.getPremises().getId() : null;
        return new MeterDto(
                meter.getId(),
                premisesId,
                meter.getResourceType(),
                meter.getSerialNumber(),
                meter.getUnitRate(),
                meter.getStatus(),
                meter.getCreatedAt()
        );
    }

    private Meter toEntity(MeterDto dto) {
        if (dto == null) {
            return null;
        }
        Meter meter = new Meter();
        meter.setId(dto.getId());
        meter.setResourceType(dto.getResourceType());
        meter.setSerialNumber(dto.getSerialNumber());
        meter.setUnitRate(dto.getUnitRate());
        meter.setStatus(dto.getStatus());
        return meter;
    }
}

