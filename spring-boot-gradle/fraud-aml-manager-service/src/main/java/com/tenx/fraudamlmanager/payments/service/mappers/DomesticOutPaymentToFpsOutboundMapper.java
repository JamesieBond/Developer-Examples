package com.tenx.fraudamlmanager.payments.service.mappers;

import com.tenx.fraudamlmanager.payments.model.api.FpsOutboundPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticOutPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DomesticOutPaymentToFpsOutboundMapper {
    DomesticOutPaymentToFpsOutboundMapper MAPPER = Mappers.getMapper(DomesticOutPaymentToFpsOutboundMapper.class);

    @Mapping(target = "creditorAccountDetails.accountNumber", source = "creditorAccountNumber")
    @Mapping(target = "creditorAccountDetails.bankId", source = "creditorSortCode")
    @Mapping(target = "debtorAccountDetails.accountNumber", source = "debtorAccountNumber")
    @Mapping(target = "debtorAccountDetails.bankId", source = "debtorSortCode")
    @Mapping(target = "amount.currency", source = "baseCurrencyCode")
    @Mapping(target = "amount.value", source = "instructedAmount")
    @Mapping(target = "amount.baseCurrency", source = "baseCurrencyCode")
    @Mapping(target = "amount.baseValue", source = "instructedAmount")
    @Mapping(target = "messageDate", source = "messageCreationDateTime", dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Mapping(target = "transactionDate", source = "transactionDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Mapping(target = "transactionNotes", ignore = true)
    @Mapping(target = "transactionTags", ignore = true)
    @Mapping(target = "existingPayee", ignore = true)
    DomesticOutPayment toFpsOutboundPayment(FpsOutboundPayment outboundpayment);

}
