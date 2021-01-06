package com.tenx.fraudamlmanager.customer.paymentinfo.domain;

import com.tenx.fraudamlmanager.customer.paymentinfo.api.AcceptedCustomerCreditTransfMapper;
import com.tenx.fraudamlmanager.customer.paymentinfo.api.RejectedCustomerCreditTransfMapper;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.CustomerCreditTransfFeedzaiConnector;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.CustomerCreditTransferException;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.CustomerCreditTransferRequestMapper;
import com.tenxbanking.events.lib.CustomerCreditTransferInitiationCompletedEvent;
import java.util.HashMap;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerCreditTransferStrategy {

  private static HashMap<String, Function<CustomerCreditTransferInitiationCompletedEvent, GenericCustomerCreditTransfType>> mapperMap = new HashMap<>();

  static {
    Function<CustomerCreditTransferInitiationCompletedEvent, GenericCustomerCreditTransfType> acceptedTypeProducer = (ev) -> AcceptedCustomerCreditTransfMapper.MAPPER
        .toAcceptedType(ev.getCustomerPaymentStatusReport());
    mapperMap.put(KnownTypes.ACCEPTED.getName(), acceptedTypeProducer);

    Function<CustomerCreditTransferInitiationCompletedEvent, GenericCustomerCreditTransfType> rejectedTypeProducer = (ev) -> RejectedCustomerCreditTransfMapper.MAPPER
        .toRejectedType(ev.getCustomerPaymentStatusReport());
    mapperMap.put(KnownTypes.REJECTED.getName(), rejectedTypeProducer);
  }

  static CustomerCreditTransferConsumer<GenericCustomerCreditTransfType> feedzaiConsumerFor(
      CustomerCreditTransfFeedzaiConnector feedzaiConnector, GenericCustomerCreditTransfType type) {

    KnownTypes transferType = KnownTypes.valueOfLabel(type.getGroupStatus());
    if (transferType == null) {
      throw new IllegalArgumentException("Group status not part of knowntypes");
    } else {
      switch (transferType) {
        case ACCEPTED:
          return accType -> feedzaiConnector
              .sendCustomerCreditTransferCheck(
                  CustomerCreditTransferRequestMapper.MAPPER
                      .toCustomerCreditTransferRequest((AcceptedCustomerCreditTransfType) accType));

        case REJECTED:
          return accType -> feedzaiConnector
              .sendCustomerCreditTransferCheck(
                  CustomerCreditTransferRequestMapper.MAPPER
                      .toCustomerCreditTransferRequest((RejectedCustomerCreditTransfType) accType));
        default:
          log.error("No feedzai connector method call defined for group status {}", type.getGroupStatus());
          throw new IllegalArgumentException(
              "No feedzai connector method call defined ");
      }
    }
  }

  static Function<CustomerCreditTransferInitiationCompletedEvent, GenericCustomerCreditTransfType> mapperForGroupStatus(
      String groupStatus) {
    return mapperMap.get(groupStatus);
  }

  static boolean isKnownType(String type) {
    return KnownTypes.valueOfLabel(type) != null;
  }

  @RequiredArgsConstructor
  @Getter
  private enum KnownTypes {
    ACCEPTED("ACSC"),
    REJECTED("RJCT");

    private final String name;

    static KnownTypes valueOfLabel(String label) {
      for (KnownTypes e : values()) {
        if (e.name.equals(label)) {
          return e;
        }
      }
      return null;
    }
  }

  interface CustomerCreditTransferConsumer<T> {

    void accept(T t) throws CustomerCreditTransferException;

  }

}



