package com.tenx.fraudamlmanager.payments.service.mappers;

import com.tenx.fraudamlmanager.payments.model.api.DirectDebitPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DirectDebitBacsPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DirectDebitBacsPaymentToDirectDebitPaymentMapper {
    DirectDebitBacsPaymentToDirectDebitPaymentMapper MAPPER = Mappers.getMapper(DirectDebitBacsPaymentToDirectDebitPaymentMapper.class);

    @Mapping(target = "creditorAccountDetails.accountNumber", source = "payee.accountNumber")
    @Mapping(target = "creditorAccountDetails.bankId", source = "payee.bankId")
    @Mapping(target = "creditorName", source = "payee.accountName")
    @Mapping(target = "debtorAccountDetails.accountNumber", source = "payer.accountNumber")
    @Mapping(target = "debtorAccountDetails.bankId", source = "payer.bankId")
    @Mapping(target = "debtorName", source = "payer.accountName")
    @Mapping(target = "amount.currency", source = "paymentAmount.currency")
    @Mapping(target = "amount.value", source = "paymentAmount.value")
    @Mapping(target = "amount.baseCurrency", source = "paymentAmount.currency")
    @Mapping(target = "amount.baseValue", source = "paymentAmount.value")
    @Mapping(target = "transactionDate", source = "processingDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Mapping(target = "transactionStatus", source = "paymentStatusReason")
    @Mapping(target = "transactionReference", source = "paymentReference")
    @Mapping(target = "messageDate", source = "processingDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Mapping(target = "transactionId", source = "id")
    DirectDebitBacsPayment toDirectDebitPayment(DirectDebitPayment directDebitPayment);

}
