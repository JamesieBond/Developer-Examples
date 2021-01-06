package com.tenx.fraudamlmanager.paymentsv3.direct.debit.infrastructure.tma;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain.DirectDebitPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain.DirectDebitTransactionMonitoringConnectorV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.InfrastructureDomainFraudCheckResponseV3Mapper;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.TransactionMonitoringClientV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class DirectDebitTransactionMonitoringConnectorImplV3 implements DirectDebitTransactionMonitoringConnectorV3 {

    private final TransactionMonitoringClientV3 transactionMonitoringClientV3;

    @Override
    public FraudCheckV3 sendFinCrimeCheck(DirectDebitPaymentV3 directCreditPaymentV3) throws TransactionMonitoringException {
        return InfrastructureDomainFraudCheckResponseV3Mapper.MAPPER.toFraudCheckV3(
                transactionMonitoringClientV3.checkFinCrimeV3(DomainInfrastructureDirectDebitMapper.MAPPER.toDirectDebitPaymentV3TMARequest(directCreditPaymentV3)));
    }
}
