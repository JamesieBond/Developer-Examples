package com.tenx.fraudamlmanager.cases.v2.infrastructure;

import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.cases.v2.domain.PaymentCaseDataStore;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentCaseDataStoreImpl implements PaymentCaseDataStore {

    private static final String CASE_CREATION_FAILED_MSG = "Case creation for payment failed: {}";
    private static final String EXCEPTION_DELETE_CASE = "Case Deletion Error, transactionId : {}";
    private static final String CASE_CREATION_FAILED_IN_DB_MSG = "Storing case creation for payment failed, transactionId : {}";
    private static final String PAYMENT_ENTITY_SAVED = "Payment Entity (including Case) saved on DB, transactionId: {}";
    private static final String PAYMENT_TYPE_RETRIEVAL_FAILED_MSG = "Failed to retrieve payment type, transactionId: {}";

    private final PaymentCaseRepositoryV2 paymentCaseRepository;

    public void createCase(String transactionId, String paymentType, CaseV2 paymentCase) {
        PaymentCaseEntityV2 paymentCaseEntity = new PaymentCaseEntityV2();
        paymentCaseEntity.setTransactionId(transactionId);
        paymentCaseEntity.setPaymentType(paymentType);
        paymentCaseEntity.setPaymentCase(paymentCase);
        paymentCaseRepository.save(paymentCaseEntity);
        log.info(PAYMENT_ENTITY_SAVED, transactionId);
    }

    public void deleteCaseByTransactionId(String transactionId) throws PaymentCaseException {
        try {
            PaymentCaseEntityV2 paymentCaseEntity = paymentCaseRepository.findByTransactionId(transactionId);
            Optional.ofNullable(paymentCaseEntity)
                    .ifPresent(paymentCaseRepository::delete);
        } catch (HibernateException e) {
            log.error(CASE_CREATION_FAILED_IN_DB_MSG, transactionId, e);
        } catch (Exception e) {
            throw new PaymentCaseException(EXCEPTION_DELETE_CASE, e);
        }
    }

    public Optional<CaseV2> findCaseByTransactionId(String transactionId) throws PaymentCaseException {
        try {
            return Optional.ofNullable(paymentCaseRepository.findByTransactionId(transactionId))
                    .map(PaymentCaseEntityV2::getPaymentCase);
        } catch (HibernateException e) {
            log.error(CASE_CREATION_FAILED_IN_DB_MSG, transactionId, e);
            return Optional.empty();
        } catch (Exception e) {
            throw new PaymentCaseException(CASE_CREATION_FAILED_MSG, e);
        }
    }

    public Optional<String> findPaymentTypeByTransactionId(String transactionId)
        throws PaymentCaseException {
        try {
            return Optional.ofNullable(paymentCaseRepository.findByTransactionId(transactionId))
                .map(PaymentCaseEntityV2::getPaymentType);
        } catch (HibernateException e) {
            log.error(PAYMENT_TYPE_RETRIEVAL_FAILED_MSG, transactionId, e);
            return Optional.empty();
        }
    }
}


