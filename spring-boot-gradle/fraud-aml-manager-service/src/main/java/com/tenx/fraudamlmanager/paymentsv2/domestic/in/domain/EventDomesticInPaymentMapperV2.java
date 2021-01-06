package com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain;

import com.tenx.fraud.payments.fpsin.FPSInboundPaymentFraudCheck;
import com.tenx.fraudamlmanager.application.DateUtils;
import java.text.ParseException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(imports = {DateUtils.class, Double.class})
public interface EventDomesticInPaymentMapperV2 {

    EventDomesticInPaymentMapperV2 MAPPER = Mappers.getMapper(EventDomesticInPaymentMapperV2.class);

    @Mapping(target = "creditorAccountDetails.accountNumber", source = "creditor.accountNumber")
    @Mapping(target = "creditorAccountDetails.bankId", source = "creditor.bankId")
    @Mapping(target = "creditorName", source = "creditor.name")
    @Mapping(target = "debtorAccountDetails.bankId", source = "debtor.bankId")
    @Mapping(target = "debtorAccountDetails.accountNumber", source = "debtor.accountNumber")
    @Mapping(target = "debtorName", source = "debtor.name")
    @Mapping(target = "balanceBefore.currency", source = "balanceBefore.currency")
    @Mapping(target = "balanceBefore.value", expression = "java( Double.valueOf( amount.getValue()))")
    @Mapping(target = "balanceBefore.baseCurrency", source = "balanceBefore.baseCurrency")
    @Mapping(target = "balanceBefore.baseValue", expression = "java( Double.valueOf(amount.getBaseValue()))")
    @Mapping(target = "transactionReference", source = "transaction.reference")
    @Mapping(target = "amount.currency", source = "transaction.amount.currency")
    @Mapping(target = "amount.value", expression = "java( Double.valueOf( fPSInTransaction.getAmount().getValue()))")
    @Mapping(target = "amount.baseCurrency", source = "transaction.amount.baseCurrency")
    @Mapping(target = "amount.baseValue", expression = "java( Double.valueOf( fPSInTransaction.getAmount().getBaseValue()))")
    @Mapping(target = "transactionId", source = "transaction.id")
    @Mapping(target = "transactionDate", expression = "java( DateUtils.getDateFromKafkaString( inboundPaymentFraudCheck.getTransaction().getDate()) )")
    @Mapping(target = "messageDate", expression = "java( DateUtils.getDateFromKafkaString( inboundPaymentFraudCheck.getTransaction().getMessageDate()) )")
    @Mapping(target = "transactionStatus", source = "transaction.status")
    @Mapping(target = "paymentType", source = "transaction.paymentTypeInformation")
    @Mapping(target = "debtorAccountDetails.accountAddress", source = "debtor.address.line")
    @Mapping(target = "creditorAccountDetails.accountName", source = "creditor.name")
    @Mapping(target = "originatingCreditInstitution", source = "transaction.originatingCreditInstitution")
    @Mapping(target = "debtorAccountDetails.iban", source = "debtor.iban")
    @Mapping(target = "debtorAccountDetails.accountName", source = "debtor.name")
    @Mapping(target = "partyKey", source = "partyKey")
    @Mapping(target = "creditorAccountDetails.accountAddress", ignore = true)
    @Mapping(target = "creditorPostalAddress", ignore = true)
    @Mapping(target = "debtorPostalAddress", ignore = true)
    DomesticInPaymentV2 toDomesticInPaymentV2(FPSInboundPaymentFraudCheck inboundPaymentFraudCheck)
            throws ParseException;

}
