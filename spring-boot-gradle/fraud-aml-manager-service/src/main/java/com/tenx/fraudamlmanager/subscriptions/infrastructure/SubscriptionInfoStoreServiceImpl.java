package com.tenx.fraudamlmanager.subscriptions.infrastructure;

import com.tenx.fraudamlmanager.subscriptions.domain.SubscriptionInfoStoreService;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionInfoStoreServiceImpl implements SubscriptionInfoStoreService {

    private final SubscriptionInfoRepository subscriptionInfoRepository;

    @Override
    public void storeSubscriptionInfo(String subscriptionKey, Date activeDate) {
        SubscriptionInfo subscriptionInfo = new SubscriptionInfo(subscriptionKey, activeDate);
        subscriptionInfoRepository.save(subscriptionInfo);
    }
}
