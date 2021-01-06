package com.tenx.fraudamlmanager.paymentsv3.onus.api;

import com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure.OnUsPaymentV3;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OnUsMapper {

    OnUsMapper MAPPER = Mappers.getMapper(OnUsMapper.class);

    @Mapping(target = "threatmetrixData", ignore = true)
    OnUsPaymentV3 toOnUs(OnUsPaymentRequestV3 onUsPaymentRequestV3);

}
