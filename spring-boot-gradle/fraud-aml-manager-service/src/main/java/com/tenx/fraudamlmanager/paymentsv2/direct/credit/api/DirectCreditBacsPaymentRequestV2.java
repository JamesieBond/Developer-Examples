package com.tenx.fraudamlmanager.paymentsv2.direct.credit.api;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectCreditBacsPaymentRequestV2 {

  @Valid
  @NotNull
  @ApiModelProperty(required = true)
  private AccountDetailsDirectCreditRequestV2 creditorAccountDetails;

  @NotEmpty
  @ApiModelProperty(required = true)
  private String creditorName;

  @Valid
  @NotNull
  @ApiModelProperty(required = true)
  private AccountDetailsDirectCreditRequestV2 debtorAccountDetails;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String debtorName;

  @Valid
  @NotNull
  @ApiModelProperty(required = true)
  private PaymentAmountDirectCreditRequestV2 amount;

    private String partyKey;

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

}
