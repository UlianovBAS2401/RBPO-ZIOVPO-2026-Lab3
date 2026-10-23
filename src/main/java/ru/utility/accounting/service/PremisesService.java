package ru.utility.accounting.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.utility.accounting.dto.PremisesDto;
import ru.utility.accounting.entity.Meter;
import ru.utility.accounting.entity.Premises;
import ru.utility.accounting.entity.User;
import ru.utility.accounting.repository.MeterReadingRepository;
import ru.utility.accounting.repository.MeterRepository;
import ru.utility.accounting.repository.PremisesRepository;
import ru.utility.accounting.repository.UserRepository;
import ru.utility.accounting.repository.UtilityChargeRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PremisesService {

    private final PremisesRepository premisesRepository;
    private final UserRepository userRepository;
    private final MeterRepository meterRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final UtilityChargeRepository utilityChargeRepository;

    public PremisesService(PremisesRepository premisesRepository,
                           UserRepository userRepository,
                           MeterRepository meterRepository,
                           MeterReadingRepository meterReadingRepository,
                           UtilityChargeRepository utilityChargeRepository) {
        this.premisesRepository = premisesRepository;
        this.userRepository = userRepository;
        this.meterRepository = meterRepository;
        this.meterReadingRepository = meterReadingRepository;
        this.utilityChargeRepository = utilityChargeRepository;
    }

    public List<Premises> findAll() {
        return premisesRepository.findAll();
    }

    public Premises findById(Long id) {
        return premisesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Помещение не найдено с id: " + id));
    }

    @Transactional
    public Premises create(Premises premises, Long userId) {
        premises.setCreatedAt(OffsetDateTime.now());

        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID владельца (User) обязателен");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь-владелец не найден с id: " + userId));
        premises.setUser(user);

        if (premises.getAddress() == null || premises.getAddress().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Адрес обязателен");
        }
        if (premises.getArea() == null || premises.getArea() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Площадь должна быть больше 0");
        }

        return premisesRepository.save(premises);
    }

    @Transactional
    public Premises patch(Long id, PremisesDto dto) {
        Premises premises = findById(id);

        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            premises.setAddress(dto.getAddress());
        }
        if (dto.getArea() != null && dto.getArea() > 0) {
            premises.setArea(dto.getArea());
        }
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден с id: " + dto.getUserId()));
            premises.setUser(user);
        }

        return premisesRepository.save(premises);
    }

    @Transactional
    public void delete(Long id) {
        Premises premises = findById(id);

        utilityChargeRepository.deleteByPremisesId(premises.getId());

        List<Meter> meters = meterRepository.findByPremisesId(premises.getId());
        for (Meter meter : meters) {
            meterReadingRepository.deleteByMeterId(meter.getId());
            meterRepository.delete(meter);
        }

        premisesRepository.delete(premises);
    }
}

