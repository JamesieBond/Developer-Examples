package com.tenx.fraudamlmanager.paymentsv3.api;

import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author Niall O'Connell
 */
@Mapper
public interface DomainApiFraudCheckResponseV3Mapper {

    DomainApiFraudCheckResponseV3Mapper MAPPER = Mappers.getMapper(
            DomainApiFraudCheckResponseV3Mapper.class);

    FraudCheckResponseV3 toFraudCheckResponseV3(FraudCheckV3 fraudCheckV3);

}
