package com.tenx.fraudamlmanager.paymentsv3.direct.credit.api;

import com.tenx.fraudamlmanager.paymentsv3.direct.credit.domain.DirectCreditPaymentV3;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApiDomainDirectCreditPaymentMapper {

    ApiDomainDirectCreditPaymentMapper MAPPER = Mappers.getMapper(ApiDomainDirectCreditPaymentMapper.class);

    DirectCreditPaymentV3 toDirectCreditPayment(DirectCreditPaymentRequestV3 directCreditPaymentRequestV3);

}
