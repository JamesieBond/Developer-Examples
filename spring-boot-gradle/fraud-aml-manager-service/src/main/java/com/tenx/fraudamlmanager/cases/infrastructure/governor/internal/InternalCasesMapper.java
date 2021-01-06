package com.tenx.fraudamlmanager.cases.infrastructure.governor.internal;

import com.tenx.fraudamlmanager.cases.domain.internal.InternalCases;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InternalCasesMapper {

  InternalCasesMapper MAPPER = Mappers.getMapper(InternalCasesMapper.class);

  InternalCasesRequest toCaseGovernorRequest(InternalCases internalCases);

}
