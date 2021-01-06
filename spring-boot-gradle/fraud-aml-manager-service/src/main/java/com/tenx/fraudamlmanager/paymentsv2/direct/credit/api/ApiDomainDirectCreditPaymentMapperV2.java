package com.tenx.fraudamlmanager.paymentsv2.direct.credit.api;

import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditBacsPaymentV2;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApiDomainDirectCreditPaymentMapperV2 {

  ApiDomainDirectCreditPaymentMapperV2 MAPPER = Mappers.getMapper(ApiDomainDirectCreditPaymentMapperV2.class);

  DirectCreditBacsPaymentV2 toDirectCreditPayment(DirectCreditBacsPaymentRequestV2 directCreditBacsPaymentRequestV2);

}
