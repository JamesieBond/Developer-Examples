package com.tenx.fraudamlmanager.paymentsv2.direct.credit.infrastructure;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.AccountDetailsDirectCreditV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditBacsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditTransactionMonitoringHandlerV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.PaymentAmountDirectCreditV2;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DirectCreditTransactionMonitoringServiceImplV2Test {


    private DirectCreditTransactionMonitoringHandlerV2 directCreditTransactionMonitoringHandlerV2;

    @MockBean
    private PaymentMetrics paymentMetrics;

    @MockBean
    private DirectCreditTransactionMonitoringClientV2 directDebitTransactionMonitoringClientV2;

    @BeforeEach
    public void beforeEach() {
        this.directCreditTransactionMonitoringHandlerV2 = new DirectCreditTransactionMonitoringHandlerImplV2(
            paymentMetrics, directDebitTransactionMonitoringClientV2);
    }

    @Test
    public void testCheckDomesticInPaymentV2()
        throws TransactionMonitoringException, DirectCreditTransactionMonitoringExceptionV2 {
        DirectCreditBacsPaymentV2 directCreditBacsPaymentV2 = new DirectCreditBacsPaymentV2(
            new AccountDetailsDirectCreditV2("1234 ", "5678"),
            "CreditorName",
            new AccountDetailsDirectCreditV2("1234", "5678"),
            "DebitorName",
            new PaymentAmountDirectCreditV2("USD", 1.00, "Euro", 2.00),
            "PartyKey",
            "TransactionId",
            new Date(),
            new Date(),
            "TransactionStatus",
            "TransactionReference"
        );

        directCreditTransactionMonitoringHandlerV2.checkDirectCreditV2(directCreditBacsPaymentV2);
        Mockito.verify(directDebitTransactionMonitoringClientV2, times(1))
            .checkFinCrimeV2DirectCredit(directCreditBacsPaymentV2);
        Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_CREDIT);

    }
}
