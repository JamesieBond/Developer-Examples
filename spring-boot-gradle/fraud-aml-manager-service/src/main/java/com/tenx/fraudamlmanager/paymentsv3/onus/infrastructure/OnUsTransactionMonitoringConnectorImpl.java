package com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.InfrastructureDomainFraudCheckResponseV3Mapper;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.TransactionMonitoringClientV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.domain.OnUsTransactionMonitoringConnector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OnUsTransactionMonitoringConnectorImpl implements OnUsTransactionMonitoringConnector {

    private final TransactionMonitoringClientV3 transactionMonitoringClientV3;

    @Override
    public FraudCheckV3 checkFinCrimeV3(OnUsPaymentV3 onUsPaymentV3) throws TransactionMonitoringException {
        return InfrastructureDomainFraudCheckResponseV3Mapper.MAPPER.toFraudCheckV3(transactionMonitoringClientV3.checkFinCrimeV3(onUsPaymentV3));
    }
}
