package com.tenx.fraudamlmanager.subscriptions.domain;

import java.util.Date;

public interface SubscriptionInfoStoreService {

    void storeSubscriptionInfo(String subscriptionKey, Date activeDate);
}
