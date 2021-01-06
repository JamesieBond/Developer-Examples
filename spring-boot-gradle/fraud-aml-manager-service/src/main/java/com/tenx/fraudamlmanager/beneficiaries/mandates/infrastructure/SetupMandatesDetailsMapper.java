package com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure;

import com.tenx.fraudamlmanager.beneficiaries.mandates.domain.SetupMandatesDetails;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {BeneficiaryAction.class})
public interface SetupMandatesDetailsMapper {

    SetupMandatesDetailsMapper MAPPER = Mappers.getMapper(SetupMandatesDetailsMapper.class);

    SetupMandates mapToSetupMandates(SetupMandatesDetails setupMandatesDetails);
}
