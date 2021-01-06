package com.tenx.fraudamlmanager.cases.domain;


import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinCrimeCheckCase {

  private final List<CaseDetails> externalCases = new ArrayList<>();
  private String transactionId;
  private FinCrimeCheckCaseStatus status;

  public boolean isReferred() {
    return FinCrimeCheckCaseStatus.REFERRED.equals(status);
  }

  public boolean isBlocked() {
    return FinCrimeCheckCaseStatus.BLOCKED.equals(status);
  }

  public boolean isPassed() {
    return FinCrimeCheckCaseStatus.PASSED.equals(status);
  }

  public boolean isCancelled() {
    return FinCrimeCheckCaseStatus.CANCELLED.equals(status);
  }

  public boolean isRejected() {
    return FinCrimeCheckCaseStatus.REJECTED.equals(status);
  }

  public boolean isUpdate() {
    return isPassed() || isRejected() || isCancelled() || isBlocked();
  }

  public boolean isFinalOutcome() {
    return isPassed() || isRejected() || isCancelled();
  }

  public boolean isExternalCaseCreated() {
    return externalCases.stream().findFirst()
        .filter(c -> c.getSourceCaseID() != null && !c.getSourceCaseID().isEmpty()).isPresent();
  }
}
