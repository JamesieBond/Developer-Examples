package com.tenx.fraudamlmanager.paymentsv3.direct.debit.api;

import com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain.DirectDebitPaymentV3;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApiDomainDirectDebitPaymentMapper {

    ApiDomainDirectDebitPaymentMapper MAPPER = Mappers.getMapper(ApiDomainDirectDebitPaymentMapper.class);

    DirectDebitPaymentV3 toDirectDebitPayment(DirectDebitPaymentRequestV3 directDebitPaymentRequestV3);

}
