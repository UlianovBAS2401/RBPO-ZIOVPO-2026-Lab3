package ru.utility.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.utility.accounting.entity.MeterReading;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    List<MeterReading> findByMeterId(Long meterId);
    void deleteByMeterId(Long meterId);
    Optional<MeterReading> findByMeterIdAndPeriod(Long meterId, String period);
    List<MeterReading> findByMeterIdOrderByRecordedAtDesc(Long meterId);
    List<MeterReading> findByMeterIdAndVerificationStatusOrderByRecordedAtDesc(Long meterId, String verificationStatus);
}
