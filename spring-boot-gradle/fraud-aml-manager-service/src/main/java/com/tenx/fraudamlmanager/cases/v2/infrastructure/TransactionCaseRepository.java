package com.tenx.fraudamlmanager.cases.v2.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCaseRepository extends JpaRepository<TransactionCaseEntity, String> {

    TransactionCaseEntity findByCaseId(String caseId);

  Optional<TransactionCaseEntity> findByTransactionId(String transactionId);
}
