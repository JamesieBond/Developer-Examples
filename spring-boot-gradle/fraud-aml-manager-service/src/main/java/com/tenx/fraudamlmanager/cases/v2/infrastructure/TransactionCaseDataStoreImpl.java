package com.tenx.fraudamlmanager.cases.v2.infrastructure;

import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCaseDataStoreImpl implements TransactionCaseDataStore {

  private static final String CASE_CREATION_FAILED_MSG = "Case creation for payment failed: {}";
  private static final String CASE_CREATION_FAILED_IN_DB_MSG =
      "Storing case creation for payment failed, transactionId : {}";
  private static final String CASE_NOT_FOUND_IN_DB_MSG =
      "Storing case creation for payment failed, transactionId : ";

  private final TransactionCaseRepository transactionCaseRepository;

  @Override
  public String findCaseIdByTransactionId(String transactionId) throws PaymentCaseException {
    return transactionCaseRepository
        .findByTransactionId(transactionId)
        .map(TransactionCaseEntity::getCaseId)
        .orElseThrow(() -> new PaymentCaseException(CASE_NOT_FOUND_IN_DB_MSG + transactionId));
  }

  public void saveTransactionCase(String transactionId, String caseId) throws PaymentCaseException {
    try {
      TransactionCaseEntity entity = new TransactionCaseEntity(transactionId, caseId);
      transactionCaseRepository.save(entity);
    } catch (HibernateException e) {
      log.error(CASE_CREATION_FAILED_IN_DB_MSG, transactionId, e);
    } catch (Exception e) {
      throw new PaymentCaseException(CASE_CREATION_FAILED_MSG, e);
    }
  }
}
