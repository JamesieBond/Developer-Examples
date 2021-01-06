package com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture;

import com.tenx.fraudamlmanager.customer.paymentinfo.domain.AcceptedCustomerCreditTransfType;
import com.tenx.fraudamlmanager.customer.paymentinfo.domain.RejectedCustomerCreditTransfType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerCreditTransferRequestMapper {

  CustomerCreditTransferRequestMapper MAPPER = Mappers.getMapper(CustomerCreditTransferRequestMapper.class);


  @Mapping(target = "senderUrgency", constant = "false")
  @Mapping(target = "recurringPayment", constant = "false")
  @Mapping(target = "channelType", constant = "telephone")
  AcceptedCustomerCreditTransferRequest toCustomerCreditTransferRequest(
      AcceptedCustomerCreditTransfType acceptedCustomerCreditTransfType);


  @Mapping(target = "infoType", constant = "notHonor")
  @Mapping(target = "channelType", constant = "telephone")
  @Mapping(target = "debtor.agent.memberIdentification", source = "debtorAgentMemberIdentification")
  @Mapping(target = "debtor.account.identification", source = "debtorAccountIdentification")
  @Mapping(target = "creditor.agent.memberIdentification", source = "creditorAgentMemberIdentification")
  @Mapping(target = "creditor.account.identification", source = "creditorAccountIdentification")
  RejectedCustomerCreditTranferRequest toCustomerCreditTransferRequest(
      RejectedCustomerCreditTransfType rejectedCustomerCreditTransfType);

}
