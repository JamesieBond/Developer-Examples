package com.tenx.fraudamlmanager.subscriptions.infrastructure;

import com.tenx.fraudamlmanager.subscriptions.domain.SubscriptionInfoStoreService;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class SubscriptionInfoStoreServiceImplTest {

    @MockBean
    private SubscriptionInfoRepository subscriptionInfoRepository;

    private static SubscriptionInfoStoreService subscriptionInfoStoreService;

    @BeforeEach
    private void init() {
        subscriptionInfoStoreService = new SubscriptionInfoStoreServiceImpl(subscriptionInfoRepository);
    }

    @Test
    public void saveSubscriptionInfoTest() {
        SubscriptionInfo sI = new SubscriptionInfo("subscriptionKey", new Date());
        subscriptionInfoStoreService.storeSubscriptionInfo(sI.getSubscriptionKey(), sI.getActiveDate());
        Mockito.verify(subscriptionInfoRepository, Mockito.times(1)).save(sI);
    }
}
