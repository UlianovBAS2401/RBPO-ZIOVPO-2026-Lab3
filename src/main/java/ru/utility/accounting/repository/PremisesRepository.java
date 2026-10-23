package ru.utility.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.utility.accounting.entity.Premises;

import java.util.List;

@Repository
public interface PremisesRepository extends JpaRepository<Premises, Long> {
    List<Premises> findByUserId(Long userId);
}

