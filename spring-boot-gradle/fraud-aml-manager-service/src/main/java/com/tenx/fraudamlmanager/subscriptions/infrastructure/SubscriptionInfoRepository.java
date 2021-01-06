package com.tenx.fraudamlmanager.subscriptions.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionInfoRepository extends JpaRepository<SubscriptionInfo, String> {

    SubscriptionInfo findBySubscriptionKey(String subscriptionKey);
}
