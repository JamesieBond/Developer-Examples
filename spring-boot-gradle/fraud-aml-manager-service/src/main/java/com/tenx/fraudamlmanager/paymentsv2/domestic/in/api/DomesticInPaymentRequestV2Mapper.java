package com.tenx.fraudamlmanager.paymentsv2.domestic.in.api;

import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DomesticInPaymentRequestV2Mapper {

  DomesticInPaymentRequestV2Mapper MAPPER = Mappers
      .getMapper(DomesticInPaymentRequestV2Mapper.class);

  @Mapping(target = "creditorAccountDetails.accountName", ignore = true)
  @Mapping(target = "creditorAccountDetails.accountAddress", ignore = true)
  @Mapping(target = "creditorAccountDetails.financialInstitutionIdentification", ignore = true)
  @Mapping(target = "debtorAccountDetails.accountName", ignore = true)
  @Mapping(target = "debtorAccountDetails.accountAddress", ignore = true)
  @Mapping(target = "debtorAccountDetails.financialInstitutionIdentification", ignore = true)
  @Mapping(target = "partyKey", ignore = true)
  @Mapping(target = "debtorAccountDetails.iban", ignore = true)
  @Mapping(target = "creditorAccountDetails.iban", ignore = true)
  @Mapping(target = "creditorPostalAddress", ignore = true)
  @Mapping(target = "debtorPostalAddress", ignore = true)
  @Mapping(target = "paymentType", ignore = true)
  @Mapping(target = "originatingCreditInstitution", ignore = true)

  DomesticInPaymentV2 domesticInPaymentRequestV2toDomesticInPaymentV2(
    DomesticInPaymentRequestV2 domesticInPaymentRequestV2);

}
