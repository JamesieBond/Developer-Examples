package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.infrastructure.transactionmanager;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.model.ResponseCodeV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FinCrimeCheckTMV2 {

  private String transactionId;

  private ResponseCodeV2 status;
}
