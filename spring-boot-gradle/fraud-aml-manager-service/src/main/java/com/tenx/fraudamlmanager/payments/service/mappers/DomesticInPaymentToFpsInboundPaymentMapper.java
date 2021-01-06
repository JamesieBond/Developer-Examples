package com.tenx.fraudamlmanager.payments.service.mappers;

import com.tenx.fraudamlmanager.payments.model.api.FpsInboundPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticInPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DomesticInPaymentToFpsInboundPaymentMapper {
    DomesticInPaymentToFpsInboundPaymentMapper MAPPER = Mappers.getMapper(DomesticInPaymentToFpsInboundPaymentMapper.class);

    @Mapping(target = "creditorAccountDetails.accountNumber", source = "creditorAccountNumber")
    @Mapping(target = "creditorAccountDetails.bankId", source = "creditorSortCode")
    @Mapping(target = "creditorName", source = "creditorAccountName")
    @Mapping(target = "debtorAccountDetails.accountNumber", source = "debtorAccountNumber")
    @Mapping(target = "debtorAccountDetails.bankId", source = "debtorSortCode")
    @Mapping(target = "amount.currency", source = "instructedAmountCurrency")
    @Mapping(target = "amount.value", source = "instructedAmount")
    @Mapping(target = "amount.baseCurrency", source = "instructedAmountCurrency")
    @Mapping(target = "amount.baseValue", source = "instructedAmount")
    @Mapping(target = "transactionDate", source = "transactionDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Mapping(target = "messageDate", ignore = true)
    DomesticInPayment toFpsInboundPayment(FpsInboundPayment inboundPayment);

}
