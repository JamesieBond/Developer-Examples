package com.tenx.fraudamlmanager.subscriptions.domain;

import com.tenx.fraudamlmanager.application.DateUtils;
import com.tenx.fraudamlmanager.subscriptions.api.Subscription;
import com.tenx.fraudamlmanager.subscriptions.api.SubscriptionException;
import com.tenx.fraudamlmanager.subscriptions.infrastructure.SubscriptionEntity;
import com.tenx.fraudamlmanager.subscriptions.repository.SubscriptionRepository;
import java.text.ParseException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private final SubscriptionInfoStoreService subscriptionInfoStoreService;

    private static final String SUBSCRIPTION_SUCCESS = "Subscription saved on DB for id: {}";
    private static final String SUBSCRIPTION_INFO_SUCCESS = "Subscription info saved on DB for id: {}";
    private static final String SUBSCRIPTION_INFO_ERROR = "Impossible to parse date, subscription not saved for id: ";

    public void saveSubscriptionEvent(Subscription subscription) {
        SubscriptionEntity subscriptionEntity = SubscriptionToSubscriptionEntityMapper.MAPPER
            .toSubscriptionEntity(subscription);
        subscriptionRepository.save(subscriptionEntity);
        log.info(SUBSCRIPTION_SUCCESS, subscription.getSubscriptionKey());
    }

    @Override
    public void saveSubscriptionInfo(Subscription subscription) throws SubscriptionException {
        if (subscription.isSubscriptionActive()) {
            try {
                Date activeDate = subscription.getUpdatedDate() == null ?
                    DateUtils.getDateFromKafkaString(subscription.getCreatedDate()) :
                    DateUtils.getDateFromKafkaString(subscription.getUpdatedDate());
                subscriptionInfoStoreService.storeSubscriptionInfo(subscription.getSubscriptionKey(), activeDate);
                log.info(SUBSCRIPTION_INFO_SUCCESS, subscription.getSubscriptionKey());
            } catch (ParseException e) {
                log.error(SUBSCRIPTION_INFO_ERROR + subscription.getSubscriptionKey(), e);
                throw new SubscriptionException(SUBSCRIPTION_INFO_ERROR + subscription.getSubscriptionKey());
            }
        }
    }
}
