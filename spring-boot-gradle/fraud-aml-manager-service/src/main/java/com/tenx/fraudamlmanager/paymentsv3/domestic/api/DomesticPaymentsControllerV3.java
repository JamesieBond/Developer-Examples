package com.tenx.fraudamlmanager.paymentsv3.domestic.api;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.api.DomainApiFraudCheckResponseV3Mapper;
import com.tenx.fraudamlmanager.paymentsv3.api.FraudCheckResponseV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticFinCrimeCheckServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticInPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutReturnPaymentV3;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Niall O'Connell
 */
@RestController
@RequestMapping("/v3/payments")
@Slf4j
@Api(tags = "payments")
public class DomesticPaymentsControllerV3 {

    @Autowired
    private DomesticFinCrimeCheckServiceV3 domesticFinCrimeCheckServiceV3;

    @Autowired
    private PaymentMetrics paymentMetrics;

    @ApiOperation(value = "Financial Crime check for DomesticOut payments")
    @PostMapping("/domesticPaymentOutboundFinCrimeCheck")
    public FraudCheckResponseV3 checkDomesticOutPaymentV3(
        @RequestBody @Valid DomesticOutPaymentRequestV3 domesticOutPaymentRequestV3,
        @RequestHeader(value = "deviceKeyId", required = false) String deviceKeyId)
        throws TransactionMonitoringException {
        log.info("checkFraudDomesticOutPayment request received with ID: {}",
            domesticOutPaymentRequestV3.getTransactionId());
        paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DOMESTIC_OUT.toString());
        DomesticOutPaymentV3 domesticOutPaymentV3 = DomesticPaymentMapper.MAPPER
                .toDomesticOut(domesticOutPaymentRequestV3);
        FraudCheckV3 fraudCheckV3 = domesticFinCrimeCheckServiceV3.checkFinCrimeV3(domesticOutPaymentV3, deviceKeyId);
        return DomainApiFraudCheckResponseV3Mapper.MAPPER.toFraudCheckResponseV3(fraudCheckV3);
    }

    @ApiOperation(value = "Financial Crime check for DomesticIn payments")
    @PostMapping("/domesticPaymentInboundFinCrimeCheck")
    public FraudCheckResponseV3 checkDomesticInPaymentV3(
        @RequestBody @Valid DomesticInPaymentRequestV3 domesticInPaymentRequestV3)
        throws TransactionMonitoringException {
        log.info("checkFraudDomesticInPayment request received with ID: {}",
            domesticInPaymentRequestV3.getTransactionId());
        paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DOMESTIC_IN.toString());
        DomesticInPaymentV3 domesticInPaymentV3 = DomesticPaymentMapper.MAPPER
                .toDomesticIn(domesticInPaymentRequestV3);
        FraudCheckV3 fraudCheckV3 = domesticFinCrimeCheckServiceV3.checkFinCrimeV3(domesticInPaymentV3);

        return DomainApiFraudCheckResponseV3Mapper.MAPPER.toFraudCheckResponseV3(fraudCheckV3);
    }

    @ApiOperation(value = "Financial Crime check for DomesticOut payments")
    @PostMapping("/domesticPaymentOutboundReturnFinCrimeCheck")
    public FraudCheckResponseV3 checkDomesticOutReturnV3(
        @RequestBody @Valid DomesticOutReturnPaymentRequestV3 domesticOutReturnPaymentRequestV3)
        throws TransactionMonitoringException {
        log.info("checkDomesticOutReturnV3 request received with ID: {}",
            domesticOutReturnPaymentRequestV3.getTransactionId());
        paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RETURNED, PaymentMetricsType.DOMESTIC_OUT.toString());
        DomesticOutReturnPaymentV3 domesticOutReturnPaymentV3 = DomesticPaymentMapper.MAPPER
                .toDomesticOutReturn(domesticOutReturnPaymentRequestV3);
        FraudCheckV3 fraudCheckV3 = domesticFinCrimeCheckServiceV3.checkFinCrimeV3(domesticOutReturnPaymentV3);
        return DomainApiFraudCheckResponseV3Mapper.MAPPER.toFraudCheckResponseV3(fraudCheckV3);
    }


}
