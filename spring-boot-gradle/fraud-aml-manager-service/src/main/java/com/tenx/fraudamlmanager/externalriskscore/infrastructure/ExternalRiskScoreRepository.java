package com.tenx.fraudamlmanager.externalriskscore.infrastructure;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalRiskScoreRepository extends JpaRepository<ExternalRiskScoreEntity, Long> {

    List<ExternalRiskScoreEntity> findByPartyKey(String partyKey);

}
