package com.tenx.fraudamlmanager.paymentsv2.direct.debit.infrastructure;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.AccountDetailsDirectDebitV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitBacsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitTransactionMonitoringHandlerV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.PaymentAmountDirectDebitV2;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DirectDebitTransactionMonitoringServiceImplV2Test {


    private DirectDebitTransactionMonitoringHandlerV2 directDebitTransactionMonitoringHandlerV2;

    @MockBean
    private PaymentMetrics paymentMetrics;

    @MockBean
    private DirectDebitTransactionMonitoringClientV2 directDebitTransactionMonitoringClientV2;

    @BeforeEach
    public void beforeEach() {
        this.directDebitTransactionMonitoringHandlerV2 = new DirectDebitTransactionMonitoringHandlerImplV2(
            paymentMetrics, directDebitTransactionMonitoringClientV2);
    }

    @Test
    public void testCheckDomesticInPaymentV2()
        throws TransactionMonitoringException, DirectDebitTransactionMonitoringExceptionV2 {
        DirectDebitBacsPaymentV2 directDebitBacsPaymentV2 = new DirectDebitBacsPaymentV2(
            new AccountDetailsDirectDebitV2("1234 ", "5678"),
            "CreditorName",
            new AccountDetailsDirectDebitV2("1234", "5678"),
            "DebitorName",
            new PaymentAmountDirectDebitV2("USD", 1.00, "Euro", 2.00),
            "PartyKey",
            "TransactionId",
            new Date(),
            new Date(),
            "TransactionStatus",
            "TransactionReference");

        directDebitTransactionMonitoringHandlerV2.checkDirectDebitV2(directDebitBacsPaymentV2);
        Mockito.verify(directDebitTransactionMonitoringClientV2, times(1))
            .checkFinCrimeV2DirectDebit(directDebitBacsPaymentV2);
        Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_DEBIT);

    }
}
