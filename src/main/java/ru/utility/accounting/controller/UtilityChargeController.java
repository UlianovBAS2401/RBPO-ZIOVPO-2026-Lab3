package ru.utility.accounting.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.utility.accounting.dto.UtilityChargeDto;
import ru.utility.accounting.entity.UtilityCharge;
import ru.utility.accounting.service.UtilityChargeService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/utility-charges")
public class UtilityChargeController {

    private final UtilityChargeService utilityChargeService;

    public UtilityChargeController(UtilityChargeService utilityChargeService) {
        this.utilityChargeService = utilityChargeService;
    }

    @GetMapping
    public ResponseEntity<List<UtilityChargeDto>> getAllCharges() {
        List<UtilityCharge> list = utilityChargeService.findAll();
        List<UtilityChargeDto> dtoList = new ArrayList<>();
        for (UtilityCharge c : list) {
            dtoList.add(toDto(c));
        }
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilityChargeDto> getChargeById(@PathVariable Long id) {
        UtilityCharge charge = utilityChargeService.findById(id);
        return ResponseEntity.ok(toDto(charge));
    }

    @PostMapping
    public ResponseEntity<UtilityChargeDto> createCharge(@RequestBody UtilityChargeDto dto) {
        UtilityCharge charge = toEntity(dto);
        UtilityCharge created = utilityChargeService.create(charge, dto.getPremisesId(), dto.getMeterId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    @PostMapping("/calculate")
    public ResponseEntity<UtilityChargeDto> calculateCharge(
            @RequestParam Long meterId,
            @RequestParam String period) {
        UtilityCharge charge = utilityChargeService.calculateCharge(meterId, period);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(charge));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<UtilityChargeDto> payCharge(@PathVariable Long id) {
        UtilityCharge paid = utilityChargeService.payCharge(id);
        return ResponseEntity.ok(toDto(paid));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UtilityChargeDto> patchCharge(@PathVariable Long id, @RequestBody UtilityChargeDto dto) {
        UtilityCharge updated = utilityChargeService.patch(id, dto);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharge(@PathVariable Long id) {
        utilityChargeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UtilityChargeDto toDto(UtilityCharge charge) {
        if (charge == null) {
            return null;
        }
        Long premisesId = (charge.getPremises() != null) ? charge.getPremises().getId() : null;
        Long meterId = (charge.getMeter() != null) ? charge.getMeter().getId() : null;
        return new UtilityChargeDto(
                charge.getId(),
                premisesId,
                meterId,
                charge.getPeriod(),
                charge.getVolume(),
                charge.getAmount(),
                charge.getStatus(),
                charge.getCreatedAt()
        );
    }

    private UtilityCharge toEntity(UtilityChargeDto dto) {
        if (dto == null) {
            return null;
        }
        UtilityCharge charge = new UtilityCharge();
        charge.setId(dto.getId());
        charge.setPeriod(dto.getPeriod());
        charge.setVolume(dto.getVolume());
        charge.setAmount(dto.getAmount());
        charge.setStatus(dto.getStatus());
        return charge;
    }
}
