package com.tenx.fraudamlmanager.subscriptions.repository;

import com.tenx.fraudamlmanager.subscriptions.infrastructure.SubscriptionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    SubscriptionEntity findBySubscriptionKey(String subscriptionKey);

    List<SubscriptionEntity> findByPartyKey(String partyKey);

}
