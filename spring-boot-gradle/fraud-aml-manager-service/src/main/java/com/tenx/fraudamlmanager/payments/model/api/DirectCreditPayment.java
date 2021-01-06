package com.tenx.fraudamlmanager.payments.model.api;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectCreditPayment {

    @NotEmpty
    @ApiModelProperty(required = true)
    private String id;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private PaymentAmount paymentAmount;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private PayAccount payee;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private PayAccount payer;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String paymentReference;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String paymentStatusReason;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String subscriptionKey;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String partyKey;

    @NotNull
    @ApiModelProperty(required = true, value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
    private Date processingDate;

}
