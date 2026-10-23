package ru.utility.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.utility.accounting.entity.Meter;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeterRepository extends JpaRepository<Meter, Long> {
    List<Meter> findByPremisesId(Long premisesId);
    Optional<Meter> findBySerialNumber(String serialNumber);
}

