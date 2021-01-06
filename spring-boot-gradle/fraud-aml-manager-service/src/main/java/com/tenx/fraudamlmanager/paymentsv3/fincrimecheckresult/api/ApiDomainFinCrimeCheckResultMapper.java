package com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.api;

import com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain.FinCrimeCheckResultV3;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApiDomainFinCrimeCheckResultMapper {

    ApiDomainFinCrimeCheckResultMapper MAPPER = Mappers.getMapper(ApiDomainFinCrimeCheckResultMapper.class);

    FinCrimeCheckResultV3 toFinCrimeCheckResultDomainV3(FinCrimeCheckResultRequestV3 finCrimeCheckResultRequestV3);

}
