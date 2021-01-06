package com.tenx.fraudamlmanager.payments.controller.v1;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.model.api.FraudCheckResponse;
import com.tenx.fraudamlmanager.payments.model.api.OnUsPayment;
import com.tenx.fraudamlmanager.payments.service.FinCrimeCheckService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This api expose REST WS for all the outbound on-Us payment.
 *
 * @author Massimo Della Rovere
 */
@RestController
@RequestMapping("/v1/payments/onUs")
@Slf4j
@Api(tags = "payments")
public class OnUsPaymentsController {

	@Autowired
	private PaymentMetrics paymentMetrics;
	
	@Autowired
	private FinCrimeCheckService finCrimeCheckService;

    /**
     * This end-point check a Fin Crime
     *
     * @param onUsPayment the input payload
     * @return a FraudFinancialCheck (the basic obj that has a field "clear" set to true)
     */
	@ApiOperation(value = "Financial Crime check for On Us payments")
	@PostMapping("/finCrimeCheck")
	public FraudCheckResponse checkOnUsPayment(@RequestBody @Valid OnUsPayment onUsPayment) throws TransactionMonitoringException {
		log.info("checkFraudOnUsPaymentV1 request received with ID: {}", onUsPayment.getTransactionId());
		paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.ON_US.toString());
		return finCrimeCheckService.checkFinCrime(onUsPayment);
	}
}
