package com.tenx.fraudamlmanager.payments.service.mappers;

import com.tenx.fraudamlmanager.payments.model.api.FraudCheckResponse;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.FraudAMLSanctionsCheckResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FraudCheckResponseToFraudCheckResponseOldMapper {
    FraudCheckResponseToFraudCheckResponseOldMapper MAPPER = Mappers.getMapper(FraudCheckResponseToFraudCheckResponseOldMapper.class);

    @Mapping(target = "responseCode", source = "status")
    @Mapping(target = "clear", ignore = true)
    FraudCheckResponse toFraudCheckResponseOld(FraudAMLSanctionsCheckResponse fraudCheckResponse);

}
