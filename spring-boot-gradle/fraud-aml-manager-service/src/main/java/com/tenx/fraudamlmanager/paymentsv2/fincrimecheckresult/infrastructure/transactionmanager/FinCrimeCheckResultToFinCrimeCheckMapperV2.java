package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.infrastructure.transactionmanager;

import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultV2;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FinCrimeCheckResultToFinCrimeCheckMapperV2 {

  FinCrimeCheckResultToFinCrimeCheckMapperV2 MAPPER = Mappers
    .getMapper(FinCrimeCheckResultToFinCrimeCheckMapperV2.class);

  FinCrimeCheckTMV2 toFinCrimeCheckTM(FinCrimeCheckResultV2 finCrimeCheckResult);

}
