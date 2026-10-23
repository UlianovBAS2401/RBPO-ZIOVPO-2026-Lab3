package ru.utility.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.utility.accounting.entity.UtilityCharge;

import java.util.List;

@Repository
public interface UtilityChargeRepository extends JpaRepository<UtilityCharge, Long> {
    List<UtilityCharge> findByPremisesId(Long premisesId);
    List<UtilityCharge> findByMeterId(Long meterId);
    void deleteByPremisesId(Long premisesId);
}

