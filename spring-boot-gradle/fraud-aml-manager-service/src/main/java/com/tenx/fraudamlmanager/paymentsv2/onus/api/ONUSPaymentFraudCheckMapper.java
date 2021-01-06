package com.tenx.fraudamlmanager.paymentsv2.onus.api;

import com.tenx.fraud.payments.onus.ONUSPaymentFraudCheck;
import com.tenx.fraudamlmanager.application.DateUtils;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import java.text.ParseException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {DateUtils.class, Double.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ONUSPaymentFraudCheckMapper {

  ONUSPaymentFraudCheckMapper MAPPER = Mappers.getMapper(ONUSPaymentFraudCheckMapper.class);

  @Mapping(target = "creditorAccountDetails.accountNumber", source = "creditor.accountNumber")
  @Mapping(target = "creditorAccountDetails.bankId", source = "creditor.bankId")
  @Mapping(target = "creditorName", source = "creditor.name")
  @Mapping(target = "debtorAccountDetails.bankId", source = "debtor.bankId")
  @Mapping(target = "debtorAccountDetails.accountNumber", source = "debtor.accountNumber")
  @Mapping(target = "debtorName", source = "debtor.name")
  @Mapping(target = "balanceBefore.currency", source = "balanceBefore.currency")
  @Mapping(target = "balanceBefore.value", source = "balanceBefore.value")
  @Mapping(target = "balanceBefore.baseCurrency", source = "balanceBefore.baseCurrency")
  @Mapping(target = "balanceBefore.baseValue", source = "balanceBefore.baseValue")
  @Mapping(target = "transactionReference", source = "transaction.reference")
  @Mapping(target = "amount.currency", source = "transaction.amount.currency")
  @Mapping(target = "amount.value", source = "transaction.amount.value")
  @Mapping(target = "amount.baseCurrency", source = "transaction.amount.baseCurrency")
  @Mapping(target = "amount.baseValue", source = "transaction.amount.baseValue")
  @Mapping(target = "transactionId", source = "transaction.debtorTransactionId")
  @Mapping(target = "transactionDate", expression = "java( DateUtils.getDateFromKafkaString(onusPaymentFraudCheck.transaction.date))")
  @Mapping(target = "messageDate", expression = "java( DateUtils.getDateFromKafkaString(onusPaymentFraudCheck.transaction.messageDate))")
  @Mapping(target = "transactionTags", source = "transaction.tags")
  @Mapping(target = "transactionStatus", source = "transaction.status")
  OnUsPaymentV2 toOnUsPayment(ONUSPaymentFraudCheck onusPaymentFraudCheck) throws ParseException;
}
