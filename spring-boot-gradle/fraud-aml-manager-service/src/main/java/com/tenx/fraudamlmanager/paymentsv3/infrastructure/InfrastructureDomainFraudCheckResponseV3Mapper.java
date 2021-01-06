package com.tenx.fraudamlmanager.paymentsv3.infrastructure;

import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.FraudAMLSanctionsCheckResponseV3;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InfrastructureDomainFraudCheckResponseV3Mapper {


    InfrastructureDomainFraudCheckResponseV3Mapper MAPPER = Mappers.getMapper(
            InfrastructureDomainFraudCheckResponseV3Mapper.class);

    FraudCheckV3 toFraudCheckV3(FraudAMLSanctionsCheckResponseV3 fraudCheckPaymentResponseV3);
}
