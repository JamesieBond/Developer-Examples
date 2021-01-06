package com.tenx.fraudamlmanager.payments.core.credittransfer.infrastructure;

import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs002;
import com.tenxbanking.iso.lib.IsoFraudCheckResponseV01;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IsoFraudCheckResponseV01Mapper {

  IsoFraudCheckResponseV01Mapper MAPPER = Mappers.getMapper(IsoFraudCheckResponseV01Mapper.class);

  @Mapping(target = "applicationHeader", source = "customerCreditTransferResponse.applicationHeader")
  @Mapping(target = "fraudCheckResponse", source = "customerCreditTransferResponse.fraudCheckResponse")
  @Mapping(target= "fraudCheckResponse.groupHeader.messageIdentification", source = "customerCreditTransferResponse.fraudCheckResponse.groupHeader.messageId")
  @Mapping(target= "fraudCheckResponse.groupHeader.creationDateTime", source = "customerCreditTransferResponse.fraudCheckResponse.groupHeader.creationDateAndTime")
  IsoFraudCheckResponseV01 toIsoFraudCheckResponseV01(Pacs002 pacs002);
}
