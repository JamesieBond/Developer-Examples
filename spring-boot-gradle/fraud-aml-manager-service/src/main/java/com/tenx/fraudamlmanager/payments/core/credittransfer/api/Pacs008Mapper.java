package com.tenx.fraudamlmanager.payments.core.credittransfer.api;

import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.CreditTransferTransaction39;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs008;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SettlementDateTimeIndication1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SettlementTimeRequest2;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SupplementaryDataEnvelope1;
import com.tenxbanking.iso.lib.IsoCreditTransferFraudCheckRequestV01;
import com.tenxbanking.iso.lib.SupplementaryDataEnvelope;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {Long.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Pacs008Mapper {
  Pacs008Mapper MAPPER = Mappers.getMapper(Pacs008Mapper.class);

  SettlementDateTimeIndication1 toSettlementDateTimeIndication1(com.tenxbanking.iso.lib.SettlementDateTimeIndication1 settlementDateTimeIndication1);
  SettlementTimeRequest2 toSettlementTimeRequest2(com.tenxbanking.iso.lib.SettlementTimeRequest2 settlementTimeRequest2);

  @Named("mapSettlementTimeIndication")
  static SettlementDateTimeIndication1 mapSettlementTimeIndication(
      com.tenxbanking.iso.lib.SettlementDateTimeIndication1 settlementDateTimeIndication1){
    if(settlementDateTimeIndication1 == null)
      return new SettlementDateTimeIndication1();
    else
      return MAPPER.toSettlementDateTimeIndication1(settlementDateTimeIndication1);
  }

  @Named("mapSettlementTimeRequest")
  static SettlementTimeRequest2 mapSettlementTimeRequest(
      com.tenxbanking.iso.lib.SettlementTimeRequest2 settlementTimeRequest2){
    if(settlementTimeRequest2 == null)
      return new SettlementTimeRequest2();
    else
      return MAPPER.toSettlementTimeRequest2(settlementTimeRequest2);
  }

  @Mapping(target = "customerCreditTransfer.applicationHeader" , source = "applicationHeader")
  @Mapping(target = "customerCreditTransfer.creditTransferFraudCheckRequest.groupHeader" , source = "creditTransferFraudCheckRequest.groupHeader")
  @Mapping(target = "customerCreditTransfer.creditTransferFraudCheckRequest.creditTransferTransactionInformation" ,
      source = "creditTransferFraudCheckRequest.creditTransferTransactionInformation") //qualifiedByName = "mapCreditTransferTransactionInformation")
  @Mapping(target = "customerCreditTransfer.creditTransferFraudCheckRequest.supplementaryData", ignore = true) //source = "creditTransferFraudCheckRequest.supplementaryData", qualifiedByName = "mapSupplementaryData")
  Pacs008 toPacs008(IsoCreditTransferFraudCheckRequestV01 isoCreditTransferFraudCheckRequestV01);

  @Mapping(target = "settlementAmount", source = "settlementAmount")
  @Mapping(target = "settlementDate", source = "settlementDate")
  @Mapping(target = "settlementPriority", source = "settlementPriority")
  @Mapping(target = "settlementTimeIndication", source = "settlementTimeIndication", qualifiedByName = "mapSettlementTimeIndication")
  @Mapping(target = "settlementTimeRequest", source = "settlementTimeRequest", qualifiedByName = "mapSettlementTimeRequest")
  CreditTransferTransaction39 toCreditTransferTransaction39(com.tenxbanking.iso.lib.CreditTransferTransaction39 creditTransferTransaction39);

  @Mapping(target = "schema", expression = "java(supplementaryDataEnvelope.getSchema$())")
  SupplementaryDataEnvelope1 toSupplementaryDataEnvelope1(SupplementaryDataEnvelope supplementaryDataEnvelope);

}

