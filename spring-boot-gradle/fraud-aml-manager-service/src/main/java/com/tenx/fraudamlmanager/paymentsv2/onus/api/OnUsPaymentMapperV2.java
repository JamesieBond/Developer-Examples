package com.tenx.fraudamlmanager.paymentsv2.onus.api;

import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OnUsPaymentMapperV2 {

  OnUsPaymentMapperV2 MAPPER = Mappers.getMapper(OnUsPaymentMapperV2.class);

  OnUsPaymentV2 toOnUsPayment(OnUsPaymentRequestV2 payment);

}
