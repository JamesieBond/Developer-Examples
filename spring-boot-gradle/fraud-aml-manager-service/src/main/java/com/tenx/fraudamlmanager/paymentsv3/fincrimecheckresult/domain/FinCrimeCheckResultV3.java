package com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain;


import com.tenx.fraudamlmanager.paymentsv3.domain.ExternalCaseDetailsV3;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

/**
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class FinCrimeCheckResultV3 {

  @NonNull
  private String transactionId;

  @NonNull
  private FinCrimeCheckResultResponseCodeV3 status;

  private List<ExternalCaseDetailsV3> externalCases;

  public boolean isReferred() {
    return FinCrimeCheckResultResponseCodeV3.REFERRED == status;
  }

  private boolean isPassed() {
    return FinCrimeCheckResultResponseCodeV3.PASSED == status;
  }

  private boolean isRejected() {
    return FinCrimeCheckResultResponseCodeV3.REJECTED == status;
  }

  private boolean isCancelled() {
    return FinCrimeCheckResultResponseCodeV3.CANCELLED == status;
  }

  public boolean isFinalOutcome() {
    return isCancelled() || isRejected() || isPassed();
  }

  public boolean hasExternalCases() {
    return !CollectionUtils.isEmpty(getExternalCases());
  }

}
