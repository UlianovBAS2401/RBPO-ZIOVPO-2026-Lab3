package ru.utility.accounting.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.utility.accounting.dto.MeterDto;
import ru.utility.accounting.entity.Meter;
import ru.utility.accounting.entity.Premises;
import ru.utility.accounting.entity.UtilityCharge;
import ru.utility.accounting.repository.MeterReadingRepository;
import ru.utility.accounting.repository.MeterRepository;
import ru.utility.accounting.repository.PremisesRepository;
import ru.utility.accounting.repository.UtilityChargeRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class MeterService {

    private final MeterRepository meterRepository;
    private final PremisesRepository premisesRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final UtilityChargeRepository utilityChargeRepository;

    public MeterService(MeterRepository meterRepository,
                        PremisesRepository premisesRepository,
                        MeterReadingRepository meterReadingRepository,
                        UtilityChargeRepository utilityChargeRepository) {
        this.meterRepository = meterRepository;
        this.premisesRepository = premisesRepository;
        this.meterReadingRepository = meterReadingRepository;
        this.utilityChargeRepository = utilityChargeRepository;
    }

    public List<Meter> findAll() {
        return meterRepository.findAll();
    }

    public Meter findById(Long id) {
        return meterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счётчик не найден с id: " + id));
    }

    @Transactional
    public Meter create(Meter meter, Long premisesId) {
        meter.setCreatedAt(OffsetDateTime.now());

        if (premisesId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID помещения обязателен");
        }
        Premises premises = premisesRepository.findById(premisesId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Помещение не найдено с id: " + premisesId));
        meter.setPremises(premises);

        if (meter.getSerialNumber() == null || meter.getSerialNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Серийный номер обязателен");
        }
        if (meter.getResourceType() == null || meter.getResourceType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Тип ресурса обязателен");
        }
        if (meter.getUnitRate() == null || meter.getUnitRate() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Тариф не может быть отрицательным");
        }
        if (meter.getStatus() == null || meter.getStatus().isBlank()) {
            meter.setStatus("ACTIVE");
        }

        return meterRepository.save(meter);
    }

    @Transactional
    public Meter patch(Long id, MeterDto dto) {
        Meter meter = findById(id);

        if (dto.getResourceType() != null && !dto.getResourceType().isBlank()) {
            meter.setResourceType(dto.getResourceType());
        }
        if (dto.getSerialNumber() != null && !dto.getSerialNumber().isBlank()) {
            meter.setSerialNumber(dto.getSerialNumber());
        }
        if (dto.getUnitRate() != null && dto.getUnitRate() >= 0) {
            meter.setUnitRate(dto.getUnitRate());
        }
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            meter.setStatus(dto.getStatus());
        }
        if (dto.getPremisesId() != null) {
            Premises premises = premisesRepository.findById(dto.getPremisesId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Помещение не найдено с id: " + dto.getPremisesId()));
            meter.setPremises(premises);
        }

        return meterRepository.save(meter);
    }

    @Transactional
    public void delete(Long id) {
        Meter meter = findById(id);

        meterReadingRepository.deleteByMeterId(meter.getId());

        List<UtilityCharge> charges = utilityChargeRepository.findByMeterId(meter.getId());
        for (UtilityCharge charge : charges) {
            charge.setMeter(null);
            utilityChargeRepository.save(charge);
        }

        meterRepository.delete(meter);
    }
}

