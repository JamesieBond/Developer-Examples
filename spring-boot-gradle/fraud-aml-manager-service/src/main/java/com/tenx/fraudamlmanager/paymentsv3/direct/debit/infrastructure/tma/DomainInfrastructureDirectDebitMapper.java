package com.tenx.fraudamlmanager.paymentsv3.direct.debit.infrastructure.tma;

import com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain.DirectDebitPaymentV3;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DomainInfrastructureDirectDebitMapper {

    DomainInfrastructureDirectDebitMapper MAPPER = Mappers.getMapper(DomainInfrastructureDirectDebitMapper.class);

    DirectDebitPaymentV3TMARequest toDirectDebitPaymentV3TMARequest(DirectDebitPaymentV3 directCreditPaymentV3);
}

