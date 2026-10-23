package ru.utility.accounting.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.utility.accounting.dto.UserDto;
import ru.utility.accounting.entity.Meter;
import ru.utility.accounting.entity.Premises;
import ru.utility.accounting.entity.User;
import ru.utility.accounting.repository.*;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PremisesRepository premisesRepository;
    private final MeterRepository meterRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final UtilityChargeRepository utilityChargeRepository;

    public UserService(UserRepository userRepository,
                       PremisesRepository premisesRepository,
                       MeterRepository meterRepository,
                       MeterReadingRepository meterReadingRepository,
                       UtilityChargeRepository utilityChargeRepository) {
        this.userRepository = userRepository;
        this.premisesRepository = premisesRepository;
        this.meterRepository = meterRepository;
        this.meterReadingRepository = meterReadingRepository;
        this.utilityChargeRepository = utilityChargeRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден с id: " + id));
    }

    @Transactional
    public User create(User user) {
        user.setCreatedAt(OffsetDateTime.now());

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email обязателен");
        }
        if (user.getFullName() == null || user.getFullName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ФИО обязательно");
        }

        return userRepository.save(user);
    }

    @Transactional
    public User patch(Long id, UserDto dto) {
        User user = findById(id);

        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }

        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = findById(id);

        List<Premises> premisesList = premisesRepository.findByUserId(user.getId());
        for (Premises premises : premisesList) {
            utilityChargeRepository.deleteByPremisesId(premises.getId());

            List<Meter> meters = meterRepository.findByPremisesId(premises.getId());
            for (Meter meter : meters) {
                meterReadingRepository.deleteByMeterId(meter.getId());
                meterRepository.delete(meter);
            }

            premisesRepository.delete(premises);
        }

        userRepository.delete(user);
    }
}

