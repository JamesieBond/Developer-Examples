package com.tenx.fraudamlmanager.customer.paymentinfo.api;

import com.tenx.fraudamlmanager.customer.paymentinfo.domain.RejectedCustomerCreditTransfType;
import com.tenx.fraudamlmanager.customer.paymentinfo.domain.types.ActiveCurrencyAndAmount;
import com.tenxbanking.iso.lib.ActiveOrHistoricCurrencyAndAmount;
import com.tenxbanking.iso.lib.BranchAndFinancialInstitutionIdentification6;
import com.tenxbanking.iso.lib.CashAccount38;
import com.tenxbanking.iso.lib.CustomerPaymentStatusReport;
import com.tenxbanking.iso.lib.OriginalTransactionReference28;
import com.tenxbanking.iso.lib.PartyIdentification135;
import com.tenxbanking.iso.lib.SupplementaryData;
import java.util.Map;
import java.util.function.Function;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RejectedCustomerCreditTransfMapper extends GenericCustomerCreditTransfMapper {

  RejectedCustomerCreditTransfMapper MAPPER = Mappers.getMapper(RejectedCustomerCreditTransfMapper.class);

  final Map<String, String> routingDestinationCodesMap = Map
      .of("AU_DE", "credit_transfer", "AU_NPP", "instant_transfer", "AU_BPAY", "bill_payment", "ON_US",
          "internal_transfer");


  @Mapping(target = "transactionTraceIdentification", ignore = true) //populated by service

  @Mapping(target = "groupStatus", ignore = true) //populated by service

  @Mapping(target = "routingDestination", expression = "java(extractRoutingDestinationFor(customerPaymentStatusReport))")

  @Mapping(target = "debtorAgentMemberIdentification", expression = "java(extractDebtorAgentMemeberIdentification(customerPaymentStatusReport))")

  @Mapping(target = "creditorAgentMemberIdentification", expression = "java(extractCreditorAgentMemberIdentification(customerPaymentStatusReport))")

  @Mapping(target = "debtorAccountIdentification", expression = "java(extractDebtorAccountIdentification(customerPaymentStatusReport))")

  @Mapping(target = "creditorAccountIdentification", expression = "java(extractCreditorAccountIdentification(customerPaymentStatusReport))")

  @Mapping(target = "instructedAmount", expression = "java(extractInstructedAmount(customerPaymentStatusReport))")

  @Mapping(target = "settlementAmount", expression = "java(extractSettlementAmount(customerPaymentStatusReport))")

  @Mapping(target = "creditorName", expression = "java(extractCreditorName(customerPaymentStatusReport))")

  @Mapping(target = "debtorName", expression = "java(extractDebtorName(customerPaymentStatusReport))")

  @Mapping(target = "creationDateTime", expression = "java(extractCreationDateTime(customerPaymentStatusReport))")

  @Mapping(target = "partyKey", expression = "java(extractPartykey(customerPaymentStatusReport))")

  @Mapping(target = "deviceId", ignore = true)

  RejectedCustomerCreditTransfType toRejectedType(CustomerPaymentStatusReport customerPaymentStatusReport);


  default String extractRoutingDestinationFor(CustomerPaymentStatusReport customerPaymentStatusReport) {
    String routingDestinationCode =
        getSupplementaryData(customerPaymentStatusReport)
            .getEnvelope()
            .getRoutingDestination();

    if (routingDestinationCode == null) {
      throw new IllegalArgumentException("Routing Destination field is empty");
    }
    return routingDestinationCodesMap.get(routingDestinationCode.toUpperCase());
  }

  default ActiveCurrencyAndAmount extractInstructedAmount(CustomerPaymentStatusReport customerPaymentStatusReport) {
    ActiveOrHistoricCurrencyAndAmount activeOrHistoricCurrencyAndAmount =
        getOriginalTransactionReference(customerPaymentStatusReport)
            .getAmount()
            .getInstructedAmount();

    return activeOrHistoricCurrencyAndAmount == null ? new ActiveCurrencyAndAmount() :
        new ActiveCurrencyAndAmount(activeOrHistoricCurrencyAndAmount.getValue(),
            activeOrHistoricCurrencyAndAmount.getCurrency());
  }


  default ActiveCurrencyAndAmount extractSettlementAmount(CustomerPaymentStatusReport customerPaymentStatusReport) {
    ActiveOrHistoricCurrencyAndAmount activeOrHistoricCurrencyAndAmount =
        getOriginalTransactionReference(customerPaymentStatusReport)
            .getInterbankSettlementAmount();

    return activeOrHistoricCurrencyAndAmount == null ? new ActiveCurrencyAndAmount()
        : new ActiveCurrencyAndAmount(activeOrHistoricCurrencyAndAmount.getValue(),
            activeOrHistoricCurrencyAndAmount.getCurrency());
  }


  default String extractCreditorName(CustomerPaymentStatusReport customerPaymentStatusReport) {

    PartyIdentification135 partyIdentification =
        getOriginalTransactionReference(customerPaymentStatusReport)
            .getCreditor()
            .getParty();

    return partyIdentification != null
        ? partyIdentification.getName() : null;
  }

  default String extractDebtorName(CustomerPaymentStatusReport customerPaymentStatusReport) {

    PartyIdentification135 partyIdentification =
        getOriginalTransactionReference(customerPaymentStatusReport)
            .getDebtor()
            .getParty();

    return partyIdentification != null
        ? partyIdentification.getName() : null;
  }

  default String extractDebtorAgentMemeberIdentification(CustomerPaymentStatusReport customerPaymentStatusReport) {
    return extractAgentMemberIdentification(customerPaymentStatusReport,
        OriginalTransactionReference28::getDebtorAgent);
  }

  default String extractCreditorAgentMemberIdentification(CustomerPaymentStatusReport customerPaymentStatusReport) {
    return extractAgentMemberIdentification(customerPaymentStatusReport,
        OriginalTransactionReference28::getCreditorAgent);
  }

  default String extractAgentMemberIdentification(CustomerPaymentStatusReport customerPaymentStatusReport,
      Function<OriginalTransactionReference28, BranchAndFinancialInstitutionIdentification6> typeCall) {

    BranchAndFinancialInstitutionIdentification6 agentType = typeCall
        .apply(getOriginalTransactionReference(customerPaymentStatusReport));

    return agentType == null ? null : agentType.getFinancialInstitutionIdentification()
        .getClearingSystemMemberIdentification()
        .getMemberIdentification();
  }


  default String extractDebtorAccountIdentification(CustomerPaymentStatusReport customerPaymentStatusReport) {
    return extractAccountIdentification(customerPaymentStatusReport,
        OriginalTransactionReference28::getDebtorAccount);

  }

  default String extractCreditorAccountIdentification(CustomerPaymentStatusReport customerPaymentStatusReport) {
    return extractAccountIdentification(customerPaymentStatusReport,
        OriginalTransactionReference28::getCreditorAccount);
  }

  default String extractAccountIdentification(CustomerPaymentStatusReport customerPaymentStatusReport,
      Function<OriginalTransactionReference28, CashAccount38> typeCall) {

    CashAccount38 accountType = typeCall.apply(getOriginalTransactionReference(customerPaymentStatusReport));
    return accountType == null ? null : accountType.getIdentification().getOther().getIdentification();

  }


  default SupplementaryData getSupplementaryData(CustomerPaymentStatusReport customerPaymentStatusReport) {
    return
        GenericCustomerCreditTransfMapper.getTransactionInformationAndStatus(customerPaymentStatusReport)
            .getSupplementaryData()
            .stream().findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Empty SuplementaryData found"));
  }

  default OriginalTransactionReference28 getOriginalTransactionReference(
      CustomerPaymentStatusReport customerPaymentStatusReport) {
    return
        GenericCustomerCreditTransfMapper.getTransactionInformationAndStatus(customerPaymentStatusReport)
            .getOriginalTransactionReference();
  }


}
