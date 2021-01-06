package com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain;

import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DirectDebitFinCrimeCheckServiceImplV2Test {

    private DirectDebitFinCrimeCheckServiceV2 directDebitFinCrimeCheckServiceV2;

    @MockBean
    private DirectDebitTransactionMonitoringHandlerV2 directDebitTransactionMonitoringHandlerV2;

    @BeforeEach
    public void beforeEach() {
        this.directDebitFinCrimeCheckServiceV2 = new DirectDebitFinCrimeCheckServiceImplV2(
            directDebitTransactionMonitoringHandlerV2);
    }

    /**
     * @throws DirectDebitTransactionMonitoringExceptionV2 Generic exception
     */

    @Test
    public void checkDirectDebitPaymentMethod() throws DirectDebitTransactionMonitoringExceptionV2 {
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

        directDebitFinCrimeCheckServiceV2.checkFinCrimeV2(directDebitBacsPaymentV2);

        Mockito.verify(directDebitTransactionMonitoringHandlerV2, times(1))
            .checkDirectDebitV2(directDebitBacsPaymentV2);

    }
}
