package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.api;

import com.tenx.fraud.payments.onus.FPSOutboundReturnPaymentFraudCheck;
import com.tenx.fraudamlmanager.application.DateUtils;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import java.text.ParseException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(imports = {DateUtils.class, Double.class})
public interface EventDomesticOutReturnPaymentMapperV2 {

  EventDomesticOutReturnPaymentMapperV2 MAPPER = Mappers.getMapper(EventDomesticOutReturnPaymentMapperV2.class);

  @Mapping(target = "creditorAccountDetails.accountNumber", source = "creditor.accountNumber")
  @Mapping(target = "creditorAccountDetails.bankId", source = "creditor.bankId")
  @Mapping(target = "creditorName", source = "creditor.name")
  @Mapping(target = "debtorAccountDetails.accountNumber", source = "debtor.accountNumber")
  @Mapping(target = "debtorAccountDetails.bankId", source = "debtor.bankId")
  @Mapping(target = "debtorName", source = "debtor.name")
  @Mapping(target = "balanceBefore.currency", source = "balanceBefore.currency")
  @Mapping(target = "balanceBefore.value", expression = "java( Double.valueOf( amount.getValue()))")
  @Mapping(target = "balanceBefore.baseCurrency", source = "balanceBefore.baseCurrency")
  @Mapping(target = "balanceBefore.baseValue", expression = "java( Double.valueOf(amount.getBaseValue()))")
  @Mapping(target = "amount.currency", source = "transaction.amount.currency")
  @Mapping(target = "amount.value", expression = "java( Double.valueOf( fPSOutReturnTransaction.getAmount().getValue()))")
  @Mapping(target = "amount.baseCurrency", source = "transaction.amount.baseCurrency")
  @Mapping(target = "amount.baseValue", expression = "java( Double.valueOf( fPSOutReturnTransaction.getAmount().getBaseValue()))")
  @Mapping(target = "transactionId", source = "transaction.id")
  @Mapping(target = "transactionDate", expression = "java( DateUtils.getDateFromKafkaString( outboundReturnPaymentFraudCheck.getTransaction().getDate()) )")
  @Mapping(target = "messageDate", expression = "java( DateUtils.getDateFromKafkaString( outboundReturnPaymentFraudCheck.getTransaction().getMessageDate()) )")
  @Mapping(target = "transactionStatus", source = "transaction.status")
  @Mapping(target = "transactionReference", source = "transaction.reference")
  @Mapping(target = "transactionTags", source = "transaction.tags")
  @Mapping(target = "existingPayee", source = "existingPayee")
  @Mapping(target = "partyKey", source = "partyKey")
  @Mapping(target = "transactionNotes", ignore = true)
  DomesticOutReturnPaymentV2 toDomesticOutReturnPaymentV2(
      FPSOutboundReturnPaymentFraudCheck outboundReturnPaymentFraudCheck) throws ParseException;

}
