package com.tenx.fraudamlmanager.cases.v2.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentCaseRepositoryV2 extends JpaRepository<PaymentCaseEntityV2, Long> {

    PaymentCaseEntityV2 findByTransactionId(String transactionId);

}
