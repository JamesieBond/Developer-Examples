package com.tenx.fraudamlmanager.subscriptions.domain;

import com.tenx.fraudamlmanager.subscriptions.api.Subscription;
import com.tenx.fraudamlmanager.subscriptions.infrastructure.SubscriptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SubscriptionToSubscriptionEntityMapper {
    SubscriptionToSubscriptionEntityMapper MAPPER = Mappers.getMapper(SubscriptionToSubscriptionEntityMapper.class);

    @Mapping(target = "subscriptionJson", source = "subscription")
    @Mapping(target = "createdDate", source = "createdDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    @Mapping(target = "updatedDate", ignore = true)
    SubscriptionEntity toSubscriptionEntity(Subscription subscription);

}