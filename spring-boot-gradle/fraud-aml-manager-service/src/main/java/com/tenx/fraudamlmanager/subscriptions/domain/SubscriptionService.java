package com.tenx.fraudamlmanager.subscriptions.domain;

import com.tenx.fraudamlmanager.subscriptions.api.Subscription;
import com.tenx.fraudamlmanager.subscriptions.api.SubscriptionException;

public interface SubscriptionService {

    void saveSubscriptionEvent(Subscription subscription);

    void saveSubscriptionInfo(Subscription subscription) throws SubscriptionException;

}
