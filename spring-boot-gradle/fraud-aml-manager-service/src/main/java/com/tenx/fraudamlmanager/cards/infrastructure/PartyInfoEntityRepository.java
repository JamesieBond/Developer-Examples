package com.tenx.fraudamlmanager.cards.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyInfoEntityRepository extends JpaRepository<PartyInfoEntity, String> {

  PartyInfoEntity findByPartyKey(String partyKey);
}
