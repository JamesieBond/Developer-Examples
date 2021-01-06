package com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure;

import com.tenx.fraud.payments.fps.FPSFraudCheckResponse;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DomesticEventMapper {

    DomesticEventMapper MAPPER = Mappers.getMapper(DomesticEventMapper.class);

    @Mapping(target = "transactionId", source = "transactionId")
    @Mapping(target = "paymentType", constant = "domesticPaymentInboundFinCrimeCheck")
    @Mapping(target = "status", expression = "java( fraudCheckResponse.getStatus().name().toUpperCase())")
    FPSFraudCheckResponse toFPSInboundResponse(FraudCheckV3 fraudCheckResponse, String transactionId);

    @Mapping(target = "transactionId", source = "transactionId")
    @Mapping(target = "paymentType", constant = "domesticPaymentOutboundFinCrimeCheck")
    @Mapping(target = "status", expression = "java( fraudCheckResponse.getStatus().name().toUpperCase())")
    FPSFraudCheckResponse toFPSOutboundResponse(FraudCheckV3 fraudCheckResponse, String transactionId);

    @Mapping(target = "transactionId", source = "transactionId")
    @Mapping(target = "paymentType", constant = "domesticPaymentOutboundReturnFinCrimeCheck")
    @Mapping(target = "status", expression = "java( fraudCheckResponse.getStatus().name().toUpperCase())")
    FPSFraudCheckResponse toFPSOutboundReturnResponse(FraudCheckV3 fraudCheckResponse, String transactionId);
}
