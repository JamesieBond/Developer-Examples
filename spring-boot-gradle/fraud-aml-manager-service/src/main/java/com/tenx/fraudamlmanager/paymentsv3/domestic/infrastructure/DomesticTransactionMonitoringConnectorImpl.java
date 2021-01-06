package com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticInPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutReturnPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.domesticTransactionMonitoringConnector;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.InfrastructureDomainFraudCheckResponseV3Mapper;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.TransactionMonitoringClientV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class DomesticTransactionMonitoringConnectorImpl implements domesticTransactionMonitoringConnector {

    private final TransactionMonitoringClientV3 transactionMonitoringClientV3;

    @Override
    public FraudCheckV3 sendFinCrimeCheck(DomesticInPaymentV3 inPaymentV3) throws TransactionMonitoringException {
        return InfrastructureDomainFraudCheckResponseV3Mapper.MAPPER.toFraudCheckV3(
                transactionMonitoringClientV3.checkFinCrimeV3(DomesticPaymentTMAMapper.MAPPER.toDomesticInTMARequest(inPaymentV3)));
    }

    @Override
    public FraudCheckV3 sendFinCrimeCheck(DomesticOutPaymentV3 outPaymentV3) throws TransactionMonitoringException {
        return InfrastructureDomainFraudCheckResponseV3Mapper.MAPPER.toFraudCheckV3(
                transactionMonitoringClientV3.checkFinCrimeV3(DomesticPaymentTMAMapper.MAPPER.toDomesticOutTMARequest(outPaymentV3)));
    }

    @Override
    public FraudCheckV3 sendFinCrimeCheck(DomesticOutReturnPaymentV3 outReturnPaymentV3) throws TransactionMonitoringException {
        return InfrastructureDomainFraudCheckResponseV3Mapper.MAPPER.toFraudCheckV3(
                transactionMonitoringClientV3.checkFinCrimeV3(DomesticPaymentTMAMapper.MAPPER.toDomesticOutReturnTMARequest(outReturnPaymentV3)));
    }
}
