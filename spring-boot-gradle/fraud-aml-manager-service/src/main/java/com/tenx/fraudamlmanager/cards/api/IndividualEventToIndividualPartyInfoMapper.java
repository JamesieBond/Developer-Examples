package com.tenx.fraudamlmanager.cards.api;

import com.tenx.fraudamlmanager.cards.domain.IndividualPartyInfo;
import com.tenxbanking.individual.event.IndividualEventV1;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IndividualEventToIndividualPartyInfoMapper {

  IndividualEventToIndividualPartyInfoMapper MAPPER = Mappers
      .getMapper(IndividualEventToIndividualPartyInfoMapper.class);

  @Mapping(target = "individualAddressList", source = "address", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
  IndividualPartyInfo toIndividualPartyInfo(IndividualEventV1 individualEventV1);
}
