package com.tenx.fraudamlmanager.cases.update.api;

import com.tenx.dub.casegovernor.event.v1.CaseEventV2;
import com.tenx.fraudamlmanager.cases.update.domain.PaymentCaseUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CaseUpdateMapper {

	 CaseUpdateMapper MAPPER = Mappers.getMapper(CaseUpdateMapper.class);

	 PaymentCaseUpdate toCaseEventData(CaseEventV2 caseEventV2);

}
