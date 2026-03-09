package com.spunit.distributedtransaction.repo;

import com.spunit.distributedtransaction.domain.SagaTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaTransactionRepository extends JpaRepository<SagaTransaction, String> {
}

