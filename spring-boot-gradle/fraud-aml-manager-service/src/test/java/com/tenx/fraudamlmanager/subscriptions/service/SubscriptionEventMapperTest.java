package com.tenx.fraudamlmanager.subscriptions.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tenx.dub.subscription.event.v1.AccountBalanceLimits;
import com.tenx.dub.subscription.event.v1.Card;
import com.tenx.dub.subscription.event.v1.CreditInterest;
import com.tenx.dub.subscription.event.v1.Currency;
import com.tenx.dub.subscription.event.v1.CustomAttributes;
import com.tenx.dub.subscription.event.v1.Document;
import com.tenx.dub.subscription.event.v1.Eligibility;
import com.tenx.dub.subscription.event.v1.FeesCharges;
import com.tenx.dub.subscription.event.v1.FundingLimits;
import com.tenx.dub.subscription.event.v1.InternalDocumentation;
import com.tenx.dub.subscription.event.v1.Limits;
import com.tenx.dub.subscription.event.v1.Overdraft;
import com.tenx.dub.subscription.event.v1.PartyRole;
import com.tenx.dub.subscription.event.v1.ProductDetails;
import com.tenx.dub.subscription.event.v1.ProductLimits;
import com.tenx.dub.subscription.event.v1.SchemeLimits;
import com.tenx.dub.subscription.event.v1.Statement;
import com.tenx.dub.subscription.event.v1.StatementV2;
import com.tenx.dub.subscription.event.v1.SubscriptionCreationRule;
import com.tenx.dub.subscription.event.v1.SubscriptionEvent;
import com.tenx.dub.subscription.event.v1.Team;
import com.tenx.dub.subscription.event.v1.TermsAndCondition;
import com.tenx.dub.subscription.event.v1.TransactionLimits;
import com.tenx.fraudamlmanager.subscriptions.api.Subscription;
import com.tenx.fraudamlmanager.subscriptions.api.SubscriptionEventToSubscriptionMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class SubscriptionEventMapperTest {
    private SubscriptionEventToSubscriptionMapper mapper = Mappers.getMapper(SubscriptionEventToSubscriptionMapper.class);

    @Test
    public void givenSubscriptionEventBasicVariablesCheckConversionSuccessful() {
        SubscriptionEvent subscriptionEvent = SubscriptionEvent.newBuilder()
                .setSubscriptionKey("abc123")
                .setPartyRoles(new ArrayList<PartyRole>())
                .setProductName("newProduct")
                .setProductKey("newKey")
                .setProductVersion(1)
                .setParentSubscriptionKey("abcParent123")
                .setAccountNumber("123456")
                .setSortCode("ABC123")
                .setSubscriptionStatus("testStatus")
                .setCreatedDate("2019-06-06")
                .setProductDetails(new ProductDetails())
                .build();
        Subscription desiredSubscription = mapper.toSubscription(subscriptionEvent);

        assertEquals(subscriptionEvent.getSubscriptionKey(), desiredSubscription.getSubscriptionKey());
        assertEquals(subscriptionEvent.getProductName(), desiredSubscription.getProductName());
        assertEquals(subscriptionEvent.getProductKey(), desiredSubscription.getProductKey());
        assertEquals(1, desiredSubscription.getProductVersion());
        assertEquals(subscriptionEvent.getParentSubscriptionKey(), desiredSubscription.getParentSubscriptionKey());
        assertEquals(subscriptionEvent.getAccountNumber(), desiredSubscription.getAccountNumber());
        assertEquals(subscriptionEvent.getSortCode(), desiredSubscription.getSortCode());
        assertEquals(subscriptionEvent.getSubscriptionStatus(), desiredSubscription.getSubscriptionStatus());
        assertEquals(subscriptionEvent.getCreatedDate(), desiredSubscription.getCreatedDate());

    }

    @Test
    public void givenSubscriptionEventPartyRolesListVariablesCheckConversionSuccessful() {
        PartyRole partyRole = PartyRole.newBuilder()
                .setPartyKey("abc123")
                .setTenantKey("123abc")
                .setRole("newRole")
                .setPartyRoleKey("roleKey")
                .setCreatedDate("2019-06-06")
                .setUpdatedDate("2019-06-06")
                .build();
        List<PartyRole> partyRoleList = new ArrayList<PartyRole>();
        partyRoleList.add(partyRole);
        SubscriptionEvent subscriptionEvent = SubscriptionEvent.newBuilder()
                .setSubscriptionKey("")
                .setPartyRoles(partyRoleList)
                .setProductName("")
                .setProductKey("")
                .setProductVersion(1)
                .setParentSubscriptionKey("")
                .setAccountNumber("")
                .setSortCode("")
                .setSubscriptionStatus("")
                .setCreatedDate("")
                .setProductDetails(new ProductDetails())
                .build();
        Subscription desiredSubscription = mapper.toSubscription(subscriptionEvent);

        assertEquals(subscriptionEvent.getPartyRoles().size(), desiredSubscription.getPartyRoles().size());
        assertEquals(subscriptionEvent.getPartyRoles().get(0).getPartyKey(), desiredSubscription.getPartyRoles().get(0).getPartyKey());
        assertEquals(subscriptionEvent.getPartyRoles().get(0).getTenantKey(), desiredSubscription.getPartyRoles().get(0).getTenantKey());
        assertEquals(subscriptionEvent.getPartyRoles().get(0).getRole(), desiredSubscription.getPartyRoles().get(0).getRole());
        assertEquals(subscriptionEvent.getPartyRoles().get(0).getPartyRoleKey(), desiredSubscription.getPartyRoles().get(0).getPartyRoleKey());
        assertEquals(subscriptionEvent.getPartyRoles().get(0).getCreatedDate(), desiredSubscription.getPartyRoles().get(0).getCreatedDate());
        assertEquals(subscriptionEvent.getPartyRoles().get(0).getUpdatedDate(), desiredSubscription.getPartyRoles().get(0).getUpdatedDate());

    }

    @Test
    public void givenSubscriptionEventProductDetailsVariablesCheckConversionSuccessful() {
        Limits limits = Limits.newBuilder()
                .setTransactionLimits(new ArrayList<TransactionLimits>())
                .setSchemeLimits(new ArrayList<SchemeLimits>())
                .setAccountBalanceLimits(new ArrayList<AccountBalanceLimits>())
                .setProductLimits(new ArrayList<ProductLimits>())
                .setFundingLimits(new ArrayList<FundingLimits>())
                .build();

        ProductDetails productDetails = ProductDetails.newBuilder()
                .setEffectiveDate("")
                .setProductKey("")
                .setTenantKey("ten123")
                .setProductCategory("")
                .setProductName("")
                .setProductType("newProduct")
                .setProductDescription("")
                .setProductSegment("")
                .setCreatedDate("")
                .setCreatedBy("")
                .setUpdatedDate("")
                .setUpdatedBy("")
                .setPublishedDate("")
                .setPublishedBy("")
                .setClosedDate("")
                .setClosedBy("")
                .setStatus("")
                .setMajorVersion(1)
                .setTags(new ArrayList<String>())
                .setSubProducts(new ArrayList<String>())
                .setRequiredExternalId(new ArrayList<String>())
                .setLimits(limits)
                .setCards(new ArrayList<Card>())
                .setTermsAndConditions(new ArrayList<TermsAndCondition>())
                .setInternalDocumentation(new InternalDocumentation())
                .setSubscriptionCreationRule(new SubscriptionCreationRule())
                .setCurrency(new Currency())
                .setTeams(new ArrayList<Team>())
                .setCustomAttributes(new ArrayList<CustomAttributes>())
                .setDocuments(new ArrayList<Document>())
                .setFeesCharges(new ArrayList<FeesCharges>())
                .setCreditInterest(new CreditInterest())
                .setOverdraft(new Overdraft())
                .setEligibility(new Eligibility())
                .setStatement(new Statement())
                .setStatements(new ArrayList<StatementV2>())
                .build();

        SubscriptionEvent subscriptionEvent = SubscriptionEvent.newBuilder()
                .setSubscriptionKey("")
                .setPartyRoles(new ArrayList<PartyRole>())
                .setProductName("")
                .setProductKey("")
                .setProductVersion(1)
                .setParentSubscriptionKey("")
                .setAccountNumber("")
                .setSortCode("")
                .setSubscriptionStatus("")
                .setCreatedDate("")
                .setProductDetails(productDetails)
                .build();

        Subscription desiredSubscription = mapper.toSubscription(subscriptionEvent);

        assertEquals(subscriptionEvent.getProductDetails().getTenantKey(), desiredSubscription.getProductDetails().getTenantKey());
        assertEquals(subscriptionEvent.getProductDetails().getProductType(), desiredSubscription.getProductDetails().getProductType());
        assertEquals(subscriptionEvent.getProductDetails().getLimits().getTransactionLimits(), desiredSubscription.getProductDetails().getLimits().getTransactionLimits());
        assertEquals(subscriptionEvent.getProductDetails().getLimits().getSchemeLimits(), desiredSubscription.getProductDetails().getLimits().getSchemeLimits());
        assertEquals(subscriptionEvent.getProductDetails().getLimits().getAccountBalanceLimits(), desiredSubscription.getProductDetails().getLimits().getAccountBalanceLimits());
        assertEquals(subscriptionEvent.getProductDetails().getLimits().getProductLimits(), desiredSubscription.getProductDetails().getLimits().getProductLimits());
        assertEquals(subscriptionEvent.getProductDetails().getLimits().getFundingLimits(), desiredSubscription.getProductDetails().getLimits().getFundingLimits());

    }
}
