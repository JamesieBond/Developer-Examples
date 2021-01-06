package com.tenx.fraudamlmanager.payments.service.mappers;

import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticInPayment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OnUsPaymentToDomesticInPaymentMapper {
    OnUsPaymentToDomesticInPaymentMapper MAPPER = Mappers.getMapper(OnUsPaymentToDomesticInPaymentMapper.class);
    DomesticInPayment toDomesticIn(com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment onUsPayment);

}
