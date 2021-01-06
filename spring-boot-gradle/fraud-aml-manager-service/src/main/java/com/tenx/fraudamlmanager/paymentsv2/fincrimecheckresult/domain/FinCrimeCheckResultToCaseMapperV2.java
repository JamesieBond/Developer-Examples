package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain;

import com.tenx.fraudamlmanager.cases.domain.FinCrimeCheckCase;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FinCrimeCheckResultToCaseMapperV2 {

  FinCrimeCheckResultToCaseMapperV2 MAPPER = Mappers.getMapper(FinCrimeCheckResultToCaseMapperV2.class);

  FinCrimeCheckCase toFinCrimeCheckCase(FinCrimeCheckResultV2 finCrimeCheckResult);

}
