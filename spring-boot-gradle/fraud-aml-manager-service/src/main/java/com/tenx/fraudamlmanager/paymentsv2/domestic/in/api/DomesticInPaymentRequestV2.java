package com.tenx.fraudamlmanager.paymentsv2.domestic.in.api;

import com.tenx.fraudamlmanager.paymentsv2.domestic.api.AccountDetailsRequestV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.api.BalanceBeforeRequestV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.api.PaymentAmountRequestV2;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO object for DomesticIn payments.
 *
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomesticInPaymentRequestV2 {

  @Valid
  @NotNull
  @ApiModelProperty(required = true)
  private AccountDetailsRequestV2 creditorAccountDetails;

  @NotEmpty
  @ApiModelProperty(required = true)
  private String creditorName;

  @Valid
  @NotNull
  @ApiModelProperty(required = true)
  private AccountDetailsRequestV2 debtorAccountDetails;

  @NotEmpty
  @ApiModelProperty(required = true)
  private String debtorName;

  @Valid
  @NotNull
  @ApiModelProperty(required = true)
  private PaymentAmountRequestV2 amount;

  @Valid
  @NotNull
  @ApiModelProperty(required = true)
  private BalanceBeforeRequestV2 balanceBefore;

  @NotEmpty
  @ApiModelProperty(required = true)
  private String transactionId;

  @NotNull
  @ApiModelProperty(required = true, value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
  private Date transactionDate;

  @ApiModelProperty(value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
  private Date messageDate;

  private String transactionStatus;

  private String transactionReference;

  public String partyKeyFromDebtorAccount() {
    AccountDetailsRequestV2 accountDetails = debtorAccountDetails;

    if (accountDetails == null) {
      return "";
    }

    String accountNumber = accountDetails.getAccountNumber();
    String bankId = accountDetails.getBankId();
    String partyKey = "";

    if (accountNumber != null && bankId != null) {
      partyKey = accountNumber + " " + bankId;
    } else if (accountNumber != null) {
      partyKey = accountNumber;
    } else if (bankId != null) {
      partyKey = bankId;
    }
    return partyKey;
  }

}
