package com.spunit.distributedtransaction.repo;

import com.spunit.distributedtransaction.domain.SagaStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {
    List<SagaStep> findBySagaIdOrderByIdAsc(String sagaId);
}

