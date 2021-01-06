package com.tenx.fraudamlmanager.subscriptions.domain;

import static org.mockito.ArgumentMatchers.any;

import com.tenx.fraudamlmanager.application.DateUtils;
import com.tenx.fraudamlmanager.subscriptions.api.Subscription;
import com.tenx.fraudamlmanager.subscriptions.api.SubscriptionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class SubscriptionServiceImplTest {

    @MockBean
    private SubscriptionInfoStoreService subscriptionInfoStoreService;

    private static SubscriptionService subscriptionService;

    @BeforeEach
    private void init() {
        subscriptionService = new SubscriptionServiceImpl(subscriptionInfoStoreService);
    }

    @Test
    public void saveSubscriptionInfoTest() throws Exception {
        Subscription subscription = createSubscription();

        subscriptionService.saveSubscriptionInfo(subscription);
        Mockito.verify(subscriptionInfoStoreService, Mockito.times(1))
            .storeSubscriptionInfo(any(), any());
    }

    @Test
    public void saveSubscriptionInfoTestSaveUpdatedDate() throws Exception {
        Subscription subscription = createSubscription();

        subscriptionService.saveSubscriptionInfo(subscription);
        Mockito.verify(subscriptionInfoStoreService, Mockito.times(1))
            .storeSubscriptionInfo(
                subscription.getSubscriptionKey(),
                DateUtils.getDateFromKafkaString(subscription.getUpdatedDate()));
    }

    @Test
    public void saveSubscriptionInfoTestCreatedDate() throws Exception {
        Subscription subscription = createSubscription();
        subscription.setUpdatedDate(null);
        subscriptionService.saveSubscriptionInfo(subscription);
        Mockito.verify(subscriptionInfoStoreService, Mockito.times(1))
            .storeSubscriptionInfo(
                subscription.getSubscriptionKey(),
                DateUtils.getDateFromKafkaString(subscription.getCreatedDate()));
    }

    @Test
    public void saveSubscriptionInfoTestParseException() {
        Subscription subscription = createSubscription();
        subscription.setUpdatedDate("date");
        // subscriptionService.saveSubscriptionInfo(subscription);
        Assertions.assertThrows(SubscriptionException.class,
            () -> subscriptionService.saveSubscriptionInfo(subscription));
    }

    @Test
    public void saveSubscriptionInfoTestNotActive() throws Exception {
        Subscription subscription = createSubscription();
        subscription.setSubscriptionStatus("ALL_BUT_ACTIVE");
        subscriptionService.saveSubscriptionInfo(subscription);
        Mockito.verify(subscriptionInfoStoreService, Mockito.times(0))
            .storeSubscriptionInfo(any(), any());
    }

    private Subscription createSubscription() {
        Subscription subscription = new Subscription();
        subscription.setCreatedDate("2018-11-25T17:58:35.584+0000");
        subscription.setUpdatedDate("2020-03-13T17:58:35.584+0000");
        subscription.setSubscriptionStatus("ACTIVE");
        subscription.setSubscriptionKey("subscriptionKey");
        return subscription;
    }
}
