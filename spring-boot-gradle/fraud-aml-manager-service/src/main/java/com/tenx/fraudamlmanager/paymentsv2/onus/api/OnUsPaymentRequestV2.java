package com.tenx.fraudamlmanager.paymentsv2.onus.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "OnUsPaymentV2")
public class OnUsPaymentRequestV2 {

  @Valid
  @NotNull
  @ApiModelProperty(required = true)
  private AccountDetailsRequestOnUsV2 creditorAccountDetails;

  @NotEmpty
  @ApiModelProperty(required = true)
  private String creditorName;

  @Valid
  @NotNull
  @ApiModelProperty(required = true)
  private AccountDetailsRequestOnUsV2 debtorAccountDetails;

  @NotEmpty
  @ApiModelProperty(required = true)
  private String debtorName;

  @Valid
  @NotNull
  @ApiModelProperty(required = true)
  private PaymentAmountRequestOnUsV2 amount;

  @Valid
  @NotNull
  @ApiModelProperty(required = true)
  private BalanceBeforeRequestOnUsV2 balanceBefore;

  private String creditorPartyKey;

  @NotEmpty
  @ApiModelProperty(required = true)
  private String debtorPartyKey;

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

  private String transactionNotes;

  private List<String> transactionTags;

  private Boolean existingPayee;

}
