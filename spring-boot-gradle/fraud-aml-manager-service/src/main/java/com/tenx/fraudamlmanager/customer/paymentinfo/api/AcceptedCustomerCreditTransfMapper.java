package com.tenx.fraudamlmanager.customer.paymentinfo.api;

import com.tenx.fraudamlmanager.customer.paymentinfo.domain.AcceptedCustomerCreditTransfType;
import com.tenxbanking.iso.lib.CustomerPaymentStatusReport;
import java.time.LocalDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AcceptedCustomerCreditTransfMapper extends GenericCustomerCreditTransfMapper {

  AcceptedCustomerCreditTransfMapper MAPPER = Mappers.getMapper(AcceptedCustomerCreditTransfMapper.class);

  @Mapping(target = "transactionTraceIdentification", ignore = true) //populated by service
  @Mapping(target = "groupStatus", ignore = true) //populated by service
  @Mapping(target = "numberOfTransactions", source = "customerPaymentStatusReport.originalGroupInformationAndStatus.originalNumberOfTransactions")
  @Mapping(target = "settlementDate", expression = "java(extractSettlementDate(customerPaymentStatusReport))")
  @Mapping(target = "creationDateTime", expression = "java(extractCreationDateTime(customerPaymentStatusReport))")
  @Mapping(target = "partyKey", expression = "java(extractPartykey(customerPaymentStatusReport))")
  @Mapping(target = "deviceId", ignore = true)
  AcceptedCustomerCreditTransfType toAcceptedType(
      CustomerPaymentStatusReport customerPaymentStatusReport);


  default LocalDate extractSettlementDate(CustomerPaymentStatusReport customerPaymentStatusReport) {
    return GenericCustomerCreditTransfMapper.getTransactionInformationAndStatus(customerPaymentStatusReport)
        .getOriginalTransactionReference()
        .getInterbankSettlementDate();

  }


}
