package com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain;

import com.tenx.fraudamlmanager.cases.domain.FinCrimeCheckCase;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FinCrimeCheckResultToCaseMapperV3 {

  FinCrimeCheckResultToCaseMapperV3 MAPPER = Mappers.getMapper(
      FinCrimeCheckResultToCaseMapperV3.class);

  FinCrimeCheckCase toFinCrimeCheckCase(FinCrimeCheckResultV3 finCrimeCheckResult);

}