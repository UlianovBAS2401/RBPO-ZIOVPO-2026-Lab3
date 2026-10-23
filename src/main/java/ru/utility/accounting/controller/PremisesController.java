package ru.utility.accounting.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.utility.accounting.dto.PremisesDto;
import ru.utility.accounting.entity.Premises;
import ru.utility.accounting.service.PremisesService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping({"/api/premises", "/api/prenises"})
public class PremisesController {

    private final PremisesService premisesService;

    public PremisesController(PremisesService premisesService) {
        this.premisesService = premisesService;
    }

    @GetMapping
    public ResponseEntity<List<PremisesDto>> getAllPremises() {
        List<Premises> list = premisesService.findAll();
        List<PremisesDto> dtoList = new ArrayList<>();
        for (Premises p : list) {
            dtoList.add(toDto(p));
        }
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PremisesDto> getPremisesById(@PathVariable Long id) {
        Premises premises = premisesService.findById(id);
        return ResponseEntity.ok(toDto(premises));
    }

    @PostMapping
    public ResponseEntity<PremisesDto> createPremises(@RequestBody PremisesDto dto) {
        Premises premises = toEntity(dto);
        Premises created = premisesService.create(premises, dto.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PremisesDto> patchPremises(@PathVariable Long id, @RequestBody PremisesDto dto) {
        Premises updated = premisesService.patch(id, dto);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePremises(@PathVariable Long id) {
        premisesService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private PremisesDto toDto(Premises premises) {
        if (premises == null) {
            return null;
        }
        Long userId = (premises.getUser() != null) ? premises.getUser().getId() : null;
        return new PremisesDto(
                premises.getId(),
                premises.getAddress(),
                premises.getArea(),
                userId,
                premises.getCreatedAt()
        );
    }

    private Premises toEntity(PremisesDto dto) {
        if (dto == null) {
            return null;
        }
        Premises premises = new Premises();
        premises.setId(dto.getId());
        premises.setAddress(dto.getAddress());
        premises.setArea(dto.getArea());
        return premises;
    }
}

