package com.tenx.fraudamlmanager.customer.paymentinfo.api;

import com.tenx.fraudamlmanager.application.DateUtils;
import com.tenxbanking.events.lib.CustomerCreditTransferInitiationCompletedEvent;
import com.tenxbanking.iso.lib.CustomerPaymentStatusReport;
import com.tenxbanking.iso.lib.PaymentTransaction105;
import com.tenxbanking.iso.lib.SupplementaryData;
import java.time.LocalDateTime;
import java.util.Optional;

public interface GenericCustomerCreditTransfMapper {

  static String extractGroupStatus(
      CustomerCreditTransferInitiationCompletedEvent customerCreditTransferInitiationCompletedEvent) {
    return
        customerCreditTransferInitiationCompletedEvent
            .getCustomerPaymentStatusReport()
            .getOriginalGroupInformationAndStatus()
            .getGroupStatus();
  }

  static String extractTransactionTraceIdentification(
      CustomerCreditTransferInitiationCompletedEvent creditTransferInitiationCompletedEvent) {

    return
        getTransactionInformationAndStatus(creditTransferInitiationCompletedEvent.getCustomerPaymentStatusReport())
            .getSupplementaryData()
            .stream().findFirst().orElseThrow(
            () -> new IllegalArgumentException("Missing supplementary data inside transactionInformationAndStatus"))
            .getEnvelope().getTransactionTraceIdentification();
  }

  static PaymentTransaction105 getTransactionInformationAndStatus(
      CustomerPaymentStatusReport customerPaymentStatusReport) {

    return customerPaymentStatusReport
        .getOriginalPaymentInformationAndStatus()
        .stream().findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Empty OriginalPaymentInformationAndStatus found"))

        .getTransactionInformationAndStatus()
        .stream().findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Empty transactionInformatonAndStatus found"));
  }

  default String extractPartykey(CustomerPaymentStatusReport customerPaymentStatusReport) {

    Optional<SupplementaryData> supplementaryData =
        customerPaymentStatusReport
            .getSupplementaryData()
            .stream()
            .filter(supplData -> supplData.getPlaceAndName()
                .equals("customerPaymentStatusReport.originalPaymentInformationAndStatus[0]"))
            .findFirst();

    return supplementaryData.map(data -> data.getEnvelope().getPartyKey()).orElse(null);


  }

  default LocalDateTime extractCreationDateTime(CustomerPaymentStatusReport customerPaymentStatusReport) {
    return DateUtils.getLocalDateTimeFromInstant(customerPaymentStatusReport.getGroupHeader().getCreationDateTime());
  }
}
