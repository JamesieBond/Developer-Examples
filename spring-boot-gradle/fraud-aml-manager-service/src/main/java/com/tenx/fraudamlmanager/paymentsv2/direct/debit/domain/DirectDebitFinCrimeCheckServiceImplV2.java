package com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectDebitFinCrimeCheckServiceImplV2 implements DirectDebitFinCrimeCheckServiceV2 {

    private final DirectDebitTransactionMonitoringHandlerV2 directDebitTransactionMonitoringHandlerV2;

    public void checkFinCrimeV2(DirectDebitBacsPaymentV2 directDebitBacsPaymentV2)
        throws DirectDebitTransactionMonitoringExceptionV2 {

        directDebitTransactionMonitoringHandlerV2.checkDirectDebitV2(directDebitBacsPaymentV2);
    }

}
