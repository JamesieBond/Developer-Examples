package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.api;

import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultV2;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FinCrimeCheckResultMapperV2 {

  FinCrimeCheckResultMapperV2 MAPPER = Mappers.getMapper(
    FinCrimeCheckResultMapperV2.class);

  FinCrimeCheckResultV2 toFinCrimeCheckResultDomainV2(FinCrimeCheckResultRequestV2 finCrimeCheckResultRequestV2);

}
