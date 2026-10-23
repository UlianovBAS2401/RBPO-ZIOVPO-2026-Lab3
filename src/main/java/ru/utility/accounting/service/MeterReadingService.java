package ru.utility.accounting.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.utility.accounting.dto.MeterReadingDto;
import ru.utility.accounting.entity.Meter;
import ru.utility.accounting.entity.MeterReading;
import ru.utility.accounting.repository.MeterReadingRepository;
import ru.utility.accounting.repository.MeterRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final MeterRepository meterRepository;

    public MeterReadingService(MeterReadingRepository meterReadingRepository, MeterRepository meterRepository) {
        this.meterReadingRepository = meterReadingRepository;
        this.meterRepository = meterRepository;
    }

    public List<MeterReading> findAll() {
        return meterReadingRepository.findAll();
    }

    public MeterReading findById(Long id) {
        return meterReadingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Показание не найдено с id: " + id));
    }

    @Transactional
    public MeterReading create(MeterReading reading, Long meterId) {
        reading.setRecordedAt(OffsetDateTime.now());

        if (meterId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID счётчика обязателен");
        }
        Meter meter = meterRepository.findById(meterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счётчик не найден с id: " + meterId));
        reading.setMeter(meter);

        if (reading.getPeriod() == null || reading.getPeriod().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Период обязателен");
        }
        if (reading.getReadingValue() == null || reading.getReadingValue() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Значение показания не может быть отрицательным");
        }

        if (meterReadingRepository.findByMeterIdAndPeriod(meterId, reading.getPeriod()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Показание для этого счётчика за период " + reading.getPeriod() + " уже существует");
        }

        if (reading.getVerificationStatus() == null || reading.getVerificationStatus().isBlank()) {
            reading.setVerificationStatus("PENDING");
        }

        return meterReadingRepository.save(reading);
    }

    @Transactional
    public MeterReading verify(Long id, String status) {
        MeterReading reading = findById(id);
        String targetStatus = (status == null || status.isBlank()) ? "VERIFIED" : status.toUpperCase();

        if (!"VERIFIED".equals(targetStatus) && !"REJECTED".equals(targetStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Допустимые статусы проверки: VERIFIED, REJECTED");
        }

        if ("VERIFIED".equals(targetStatus)) {
            List<MeterReading> previousReadings = meterReadingRepository.findByMeterIdAndVerificationStatusOrderByRecordedAtDesc(
                    reading.getMeter().getId(), "VERIFIED"
            );

            for (MeterReading prev : previousReadings) {
                if (!prev.getId().equals(reading.getId()) && prev.getRecordedAt().isBefore(reading.getRecordedAt())) {
                    if (reading.getReadingValue() < prev.getReadingValue()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Новое показание (" + reading.getReadingValue() + ") не может быть меньше предыдущего подтверждённого (" + prev.getReadingValue() + ")");
                    }
                    break;
                }
            }
        }

        reading.setVerificationStatus(targetStatus);
        return meterReadingRepository.save(reading);
    }

    @Transactional
    public MeterReading patch(Long id, MeterReadingDto dto) {
        MeterReading reading = findById(id);

        if (dto.getPeriod() != null && !dto.getPeriod().isBlank()) {
            reading.setPeriod(dto.getPeriod());
        }
        if (dto.getReadingValue() != null && dto.getReadingValue() >= 0) {
            reading.setReadingValue(dto.getReadingValue());
        }
        if (dto.getVerificationStatus() != null && !dto.getVerificationStatus().isBlank()) {
            reading.setVerificationStatus(dto.getVerificationStatus());
        }
        if (dto.getMeterId() != null) {
            Meter meter = meterRepository.findById(dto.getMeterId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счётчик не найден с id: " + dto.getMeterId()));
            reading.setMeter(meter);
        }

        return meterReadingRepository.save(reading);
    }

    @Transactional
    public void delete(Long id) {
        MeterReading reading = findById(id);
        meterReadingRepository.delete(reading);
    }
}
