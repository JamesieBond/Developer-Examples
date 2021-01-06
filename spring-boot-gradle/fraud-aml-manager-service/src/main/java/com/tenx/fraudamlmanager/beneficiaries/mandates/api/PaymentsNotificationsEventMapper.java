package com.tenx.fraudamlmanager.beneficiaries.mandates.api;

import com.tenx.fraudamlmanager.beneficiaries.mandates.domain.SetupMandatesDetails;
import com.tenx.payment.configuration.directdebit.event.v1.DirectDebitEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentsNotificationsEventMapper {

    PaymentsNotificationsEventMapper MAPPER = Mappers.getMapper(PaymentsNotificationsEventMapper.class);

    @Mapping(target = "partyKey", source = "partyKey")
    @Mapping(target = "reference", source = "bacsDDMandateRef")
    @Mapping(target = "accountName", source = "creditorAccountName")
    @Mapping(target = "directDebitKey", source = "directDebitKey")
    @Mapping(target = "action", source = "action")
    SetupMandatesDetails mapToSetupMandatesDetails(DirectDebitEvent directDebitEvent);
}
