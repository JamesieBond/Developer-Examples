package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain;


import java.util.List;
import javax.validation.constraints.NotEmpty;
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
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class FinCrimeCheckResultV2 {

  @NotEmpty
  @NonNull
  private String transactionId;

  @NonNull
  private FinCrimeCheckResultResponseCodeV2 status;

  private List<ExternalCaseDetailsV2> externalCases;

  public boolean isReferred() {
    return FinCrimeCheckResultResponseCodeV2.REFERRED == status;
  }

  private boolean isPassed() {
    return FinCrimeCheckResultResponseCodeV2.PASSED == status;
  }

  private boolean isRejected() {
    return FinCrimeCheckResultResponseCodeV2.REJECTED == status;
  }

  private boolean isCancelled() {
    return FinCrimeCheckResultResponseCodeV2.CANCELLED == status;
  }

  public boolean isFinalOutcome() {
    return isCancelled() || isRejected() || isPassed();
  }

  public boolean hasExternalCases() {
    return !CollectionUtils.isEmpty(getExternalCases());
  }

}
