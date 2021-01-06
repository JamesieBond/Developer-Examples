package com.tenx.fraudamlmanager.subscriptions.api;

import com.tenx.dub.subscription.event.v1.SubscriptionEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SubscriptionEventToSubscriptionMapper {
    SubscriptionEventToSubscriptionMapper MAPPER = Mappers.getMapper(SubscriptionEventToSubscriptionMapper.class);

    @Mapping(target = "parentSubscription", ignore = true)
    @Mapping(target = "productType", ignore = true)
    @Mapping(target = "tenantKey", ignore = true)
    @Mapping(target = "activeStatus", ignore = true)
    Subscription toSubscription(SubscriptionEvent s);

}
