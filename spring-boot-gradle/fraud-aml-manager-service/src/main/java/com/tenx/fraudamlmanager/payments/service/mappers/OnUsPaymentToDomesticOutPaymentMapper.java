package com.tenx.fraudamlmanager.payments.service.mappers;

import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticOutPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OnUsPaymentToDomesticOutPaymentMapper {
    OnUsPaymentToDomesticOutPaymentMapper MAPPER = Mappers.getMapper(OnUsPaymentToDomesticOutPaymentMapper.class);

    @Mapping(target = "partyKey", source = "creditorPartyKey")
    DomesticOutPayment toDomesticOut(com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment onUsPayment);
}
