package com.tenx.fraudamlmanager.cards.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IndividualPartyInfoServiceImpl implements IndividualPartyInfoService {

  private final PartyInfoStoreService partyInfoStoreService;

  @Override
  public void storePartyInfo(IndividualPartyInfo individualPartyInfo) {
    if (individualPartyInfo.isStatusProvisioned()) {
      individualPartyInfo.getIndividualAddressList().stream()
          .filter(IndividualAddress::isStatusActive)
          .findFirst()
          .ifPresent(
              a -> partyInfoStoreService
                  .storePartyInfo(individualPartyInfo.getPartyKey(), a.getPostCode())
          );
    }
  }
}
