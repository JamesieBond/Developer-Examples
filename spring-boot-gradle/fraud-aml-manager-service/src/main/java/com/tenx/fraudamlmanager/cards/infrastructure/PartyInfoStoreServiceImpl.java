package com.tenx.fraudamlmanager.cards.infrastructure;

import com.tenx.fraudamlmanager.cards.domain.PartyInfoStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyInfoStoreServiceImpl implements PartyInfoStoreService {

  private final PartyInfoEntityRepository partyInfoEntityRepository;

  @Override
  public void storePartyInfo(String partyKey, String postCode) {
    PartyInfoEntity partyInfoEntity = new PartyInfoEntity(partyKey, postCode);
    partyInfoEntityRepository.save(partyInfoEntity);
  }
}
