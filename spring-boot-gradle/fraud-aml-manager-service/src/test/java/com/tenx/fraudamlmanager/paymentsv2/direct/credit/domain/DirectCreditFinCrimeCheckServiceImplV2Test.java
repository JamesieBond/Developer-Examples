package com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain;

import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DirectCreditFinCrimeCheckServiceImplV2Test {

    private DirectCreditFinCrimeCheckServiceV2 directCreditFinCrimeCheckServiceV2;

    @MockBean
    private DirectCreditTransactionMonitoringHandlerV2 directCreditTransactionMonitoringHandlerV2;

    @BeforeEach
    public void beforeEach() {
        this.directCreditFinCrimeCheckServiceV2 = new DirectCreditFinCrimeCheckServiceImplV2(
            directCreditTransactionMonitoringHandlerV2);
    }

    /**
     * @throws DirectCreditTransactionMonitoringExceptionV2 Generic exception
     */

    @Test
    public void checkDirectDebitPaymentMethod() throws DirectCreditTransactionMonitoringExceptionV2 {
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
        directCreditFinCrimeCheckServiceV2.checkFinCrimeDirectCreditV2(directCreditBacsPaymentV2);

        Mockito.verify(directCreditTransactionMonitoringHandlerV2, times(1))
            .checkDirectCreditV2(directCreditBacsPaymentV2);

    }
}
