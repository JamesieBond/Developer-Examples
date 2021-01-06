package com.tenx.fraudamlmanager.subscriptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.tenx.dub.subscription.event.v1.ExternalIdentifiers;
import com.tenx.dub.subscription.event.v1.PartyRole;
import com.tenx.dub.subscription.event.v1.ProductDetails;
import com.tenx.dub.subscription.event.v1.SubscriptionEvent;
import com.tenx.dub.subscription.event.v1.TsAndCsPartyConsent;
import com.tenx.fraudamlmanager.application.DateUtils;
import com.tenx.fraudamlmanager.subscriptions.api.SubscriptionException;
import com.tenx.fraudamlmanager.subscriptions.api.SubscriptionsInfoListener;
import com.tenx.fraudamlmanager.subscriptions.domain.SubscriptionInfoStoreService;
import com.tenx.fraudamlmanager.subscriptions.domain.SubscriptionService;
import com.tenx.fraudamlmanager.subscriptions.domain.SubscriptionServiceImpl;
import com.tenx.fraudamlmanager.subscriptions.infrastructure.SubscriptionInfo;
import com.tenx.fraudamlmanager.subscriptions.infrastructure.SubscriptionInfoRepository;
import com.tenx.fraudamlmanager.subscriptions.infrastructure.SubscriptionInfoStoreServiceImpl;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
@Sql({"/schema-hsqldb.sql"})
public class SubscriptionInfoIntegrationTest {

    private SubscriptionsInfoListener subscriptionsInfoListener;

    private SubscriptionService subscriptionService;

    @Autowired
    private SubscriptionInfoRepository subscriptionInfoRepository;

    private SubscriptionInfoStoreService subscriptionInfoStoreService;

    @Mock
    private Acknowledgment acknowledgment;

    @BeforeEach
    public void initTest() {
        subscriptionInfoStoreService = new SubscriptionInfoStoreServiceImpl(subscriptionInfoRepository);
        subscriptionService = new SubscriptionServiceImpl(subscriptionInfoStoreService);
        subscriptionsInfoListener = new SubscriptionsInfoListener(subscriptionService);
    }

    /**
     * Test is supposed to store createdDate
     */
    @Test
    public void testStoreSubscriptionInfoGivenUpdateDateIsNull() throws SubscriptionException, ParseException {
        SubscriptionEvent subscriptionEvent = createSubcriptionEvent();

        ConsumerRecord<String, SubscriptionEvent> consumerRecord =
            new ConsumerRecord<>("topic", 0, 0, "key", subscriptionEvent);
        subscriptionEvent.setSubscriptionStatus("ACTIVE");
        subscriptionEvent.setUpdatedDate(null);
        subscriptionsInfoListener.handleSubscriptionEvent(consumerRecord, acknowledgment);
        SubscriptionInfo subscriptionInfoEntity = subscriptionInfoRepository
            .findBySubscriptionKey(subscriptionEvent.getSubscriptionKey());
        assertThat(subscriptionInfoEntity.getSubscriptionKey()).isEqualTo(subscriptionEvent.getSubscriptionKey());
        assertThat(subscriptionInfoEntity.getActiveDate())
            .isEqualTo(DateUtils.getDateFromKafkaString(subscriptionEvent.getCreatedDate()));
    }

    /**
     * Test is supposed to store updatedDate
     */
    @Test
    public void testStoreSubscriptionInfoGivenUpdateDateIsNotNull() throws SubscriptionException, ParseException {
        SubscriptionEvent subscriptionEvent = createSubcriptionEvent();

        ConsumerRecord<String, SubscriptionEvent> consumerRecord =
            new ConsumerRecord<>("topic", 0, 0, "key", subscriptionEvent);
        subscriptionEvent.setSubscriptionStatus("ACTIVE");
        subscriptionsInfoListener.handleSubscriptionEvent(consumerRecord, acknowledgment);
        SubscriptionInfo subscriptionInfoEntity = subscriptionInfoRepository
            .findBySubscriptionKey(subscriptionEvent.getSubscriptionKey());
        assertThat(subscriptionInfoEntity.getSubscriptionKey()).isEqualTo(subscriptionEvent.getSubscriptionKey());
        assertThat(subscriptionInfoEntity.getActiveDate())
            .isEqualTo(DateUtils.getDateFromKafkaString(subscriptionEvent.getUpdatedDate()));
    }

    @Test
    public void testStoreSubscriptionInfoGivenStatusIsNotActive() throws SubscriptionException {
        SubscriptionEvent subscriptionEvent = createSubcriptionEvent();
        subscriptionEvent.setSubscriptionStatus("NOT_ACTIVE");
        ConsumerRecord<String, SubscriptionEvent> consumerRecord =
            new ConsumerRecord<>("topic", 0, 0, "key", subscriptionEvent);
        subscriptionsInfoListener.handleSubscriptionEvent(consumerRecord, acknowledgment);
        SubscriptionInfo subscriptionInfo = subscriptionInfoRepository
            .findBySubscriptionKey(subscriptionEvent.getSubscriptionKey());
        assertThat(subscriptionInfo).isNull();
    }

    private SubscriptionEvent createSubcriptionEvent() {
        return SubscriptionEvent.newBuilder()
            .setAccountNumber("account_number")
            .setCreatedDate("2020-03-13T17:58:35.584+0000")
            .setEffectiveDate("2020-05-13T17:58:35.584+0000")
            .setExternalIdentifiers(new ExternalIdentifiers("2020-03-13T17:58:35.584+0000", "bban", "iben"))
            .setLinkedSubscriptionKey("linkedSubscriptionKey")
            .setParentAccountNumber("parentAccountNumber")
            .setParentSubscriptionKey("parentSubscriptionKey")
            .setPartyRoles(Arrays
                .asList(PartyRole.newBuilder().setCreatedDate(ZonedDateTime.now().toString()).setPartyKey("partyKey")
                    .setPartyRoleKey("partyRoleKey").setRole("role").setTenantKey("tenantKey")
                    .setUpdatedDate("2020-03-13T17:58:35.584+0000")
                    .build()))
            .setProductDetails(new ProductDetails())
            .setProductKey("productKet")
            .setProductName("productName")
            .setProductVersion(1)
            .setSortCode("sortCode")
            .setSubscriptionKey("subscriptionKey")
            .setSubscriptionStatus("subscriptionStatus")
            .setTsAndCsPartyConsents(Arrays.asList(TsAndCsPartyConsent.newBuilder().build()))
            .setUpdatedDate("2018-11-25T17:58:35.584+0000")
            .build();
    }

}