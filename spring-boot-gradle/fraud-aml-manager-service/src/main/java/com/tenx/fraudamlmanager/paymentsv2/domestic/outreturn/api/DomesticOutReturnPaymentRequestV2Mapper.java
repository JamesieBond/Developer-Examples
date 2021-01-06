package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.api;

import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DomesticOutReturnPaymentRequestV2Mapper {

  DomesticOutReturnPaymentRequestV2Mapper MAPPER = Mappers
      .getMapper(DomesticOutReturnPaymentRequestV2Mapper.class);

  DomesticOutReturnPaymentV2 domesticOutReturnPaymentRequestV2toDomesticOutReturnPaymentV2(
      DomesticOutReturnPaymentRequestV2 domesticOutReturnPaymentRequestV2);


}
