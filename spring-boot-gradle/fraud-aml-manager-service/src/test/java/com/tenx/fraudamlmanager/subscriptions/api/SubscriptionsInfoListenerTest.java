package com.tenx.fraudamlmanager.subscriptions.api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

import com.tenx.dub.subscription.event.v1.ExternalIdentifiers;
import com.tenx.dub.subscription.event.v1.PartyRole;
import com.tenx.dub.subscription.event.v1.ProductDetails;
import com.tenx.dub.subscription.event.v1.SubscriptionEvent;
import com.tenx.dub.subscription.event.v1.TsAndCsPartyConsent;
import com.tenx.fraudamlmanager.SpringBootTestBase;
import com.tenx.fraudamlmanager.subscriptions.domain.SubscriptionService;
import java.time.ZonedDateTime;
import java.util.Arrays;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.Acknowledgment;

public class SubscriptionsInfoListenerTest extends SpringBootTestBase {

  @Captor
  private ArgumentCaptor<Subscription> subscriptionsCaptor;

  @MockBean
  private SubscriptionService subscriptionService;

  @Mock
  private Acknowledgment acknowledgment;

  private SubscriptionsInfoListener subscriptionsInfoListener;

  @BeforeEach
  void initTest() {
    subscriptionsInfoListener = new SubscriptionsInfoListener(subscriptionService);
  }

  @Test
  void testProcessIndividualPartyEvent() throws SubscriptionException {
    SubscriptionEvent subscriptionEvent = createSubcriptionEvent();

    ConsumerRecord<String, SubscriptionEvent> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", subscriptionEvent);

    subscriptionsInfoListener.handleSubscriptionEvent(consumerRecord, acknowledgment);

    Mockito.verify(subscriptionService, times(1)).saveSubscriptionInfo(subscriptionsCaptor.capture());
    Mockito.verify(acknowledgment, times(1)).acknowledge();
  }

  @Test
  void testProcessIndividualPartyEventGivenNPEIsThrown() throws SubscriptionException {
    SubscriptionEvent subscriptionEvent = createSubcriptionEvent();

    ConsumerRecord<String, SubscriptionEvent> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", subscriptionEvent);

    doThrow(new NullPointerException("test")).when(subscriptionService)
        .saveSubscriptionInfo(any());
    assertThrows(
        NullPointerException.class, () ->
            subscriptionsInfoListener.handleSubscriptionEvent(consumerRecord, acknowledgment));
    Mockito.verify(subscriptionService, times(1)).saveSubscriptionInfo(subscriptionsCaptor.capture());
    Mockito.verify(acknowledgment, times(0)).acknowledge();
  }

  @Test
  void testProcessIndividualPartyEventGivenxceptionIsThrown() throws SubscriptionException {
    SubscriptionEvent subscriptionEvent = createSubcriptionEvent();

    ConsumerRecord<String, SubscriptionEvent> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", subscriptionEvent);

    doThrow(new SubscriptionException("test")).when(subscriptionService)
        .saveSubscriptionInfo(any());
    assertDoesNotThrow(() -> subscriptionsInfoListener.handleSubscriptionEvent(consumerRecord, acknowledgment));
    Mockito.verify(subscriptionService, times(1)).saveSubscriptionInfo(subscriptionsCaptor.capture());
    Mockito.verify(acknowledgment, times(1)).acknowledge();
  }

  private SubscriptionEvent createSubcriptionEvent() {
    return SubscriptionEvent.newBuilder()
        .setAccountNumber("account_number")
        .setCreatedDate(ZonedDateTime.now().toString())
        .setEffectiveDate(ZonedDateTime.now().toString())
        .setExternalIdentifiers(new ExternalIdentifiers(ZonedDateTime.now().toString(), "bban", "iben"))
        .setLinkedSubscriptionKey("linkedSubscriptionKey")
        .setParentAccountNumber("parentAccountNumber")
        .setParentSubscriptionKey("parentSubscriptionKey")
        .setPartyRoles(Arrays
            .asList(PartyRole.newBuilder().setCreatedDate(ZonedDateTime.now().toString()).setPartyKey("partyKey")
                .setPartyRoleKey("partyRoleKey").setRole("role").setTenantKey("tenantKey")
                .setUpdatedDate(ZonedDateTime.now().toString())
                .build()))
        .setProductDetails(new ProductDetails())
        .setProductKey("productKet")
        .setProductName("productName")
        .setProductVersion(1)
        .setSortCode("sortCode")
        .setSubscriptionKey("subscriptionKey")
        .setSubscriptionStatus("subscriptionStatus")
        .setTsAndCsPartyConsents(Arrays.asList(TsAndCsPartyConsent.newBuilder().build()))
        .setUpdatedDate(ZonedDateTime.now().toString())
        .build();
  }

}
