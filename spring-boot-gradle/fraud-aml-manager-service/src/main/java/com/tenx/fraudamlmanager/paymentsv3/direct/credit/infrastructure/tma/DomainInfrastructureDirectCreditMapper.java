package com.tenx.fraudamlmanager.paymentsv3.direct.credit.infrastructure.tma;

import com.tenx.fraudamlmanager.paymentsv3.direct.credit.domain.DirectCreditPaymentV3;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DomainInfrastructureDirectCreditMapper {

    DomainInfrastructureDirectCreditMapper MAPPER = Mappers.getMapper(DomainInfrastructureDirectCreditMapper.class);

    DirectCreditPaymentV3TMARequest toDirectCreditPaymentV3TMARequest(DirectCreditPaymentV3 directCreditPaymentV3);
}

