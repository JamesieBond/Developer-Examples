package com.tenx.fraudamlmanager.paymentsv2.direct.debit.api;

import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitBacsPaymentV2;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApiDomainDirectDebitPaymentMapperV2 {

    ApiDomainDirectDebitPaymentMapperV2 MAPPER = Mappers.getMapper(ApiDomainDirectDebitPaymentMapperV2.class);

    DirectDebitBacsPaymentV2 toDirectDebitPayment(DirectDebitBacsPaymentRequestV2 directDebitBacsPaymentRequestV2);

}
