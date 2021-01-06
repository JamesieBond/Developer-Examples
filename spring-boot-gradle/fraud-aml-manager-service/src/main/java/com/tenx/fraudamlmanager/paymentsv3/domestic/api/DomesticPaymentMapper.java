package com.tenx.fraudamlmanager.paymentsv3.domestic.api;

import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticInPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutReturnPaymentV3;
import javax.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DomesticPaymentMapper {

    DomesticPaymentMapper MAPPER = Mappers.getMapper(DomesticPaymentMapper.class);

    @Mapping(target = "threatmetrixData", ignore = true)
    DomesticOutPaymentV3 toDomesticOut(DomesticOutPaymentRequestV3 domesticOutPaymentRequestV3);

    DomesticOutReturnPaymentV3 toDomesticOutReturn(@Valid DomesticOutReturnPaymentRequestV3 domesticOutPaymentRequestV3);

    DomesticInPaymentV3 toDomesticIn(DomesticInPaymentRequestV3 domesticInPaymentRequestV3);
}
