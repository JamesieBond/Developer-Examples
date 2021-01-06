package com.tenx.logging.util;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.core.ContextBase;
import org.junit.jupiter.api.Test;


class PIIConverterTest {


  private PIIConverter piiConverter = new PIIConverter();


  @Test
  void testRemoveName() {
    String message = "The customer name is X";
    String maskedMessage = piiConverter.mask(message);
    assertThat(maskedMessage).isEqualTo(PIIConverter.getPiiMessageWarning());
  }

  @Test
  void testRemoveNameJson() {
    String message = " {\"givenName\": \"Jassedqis\"}";
    String maskedMessage = piiConverter.mask(message);
    assertThat(maskedMessage).isEqualTo(PIIConverter.getPiiMessageWarning());
  }

  @Test
  void testRemoveAddressLineX() {
    String message = "AddressLine3 : abc";
    String maskedMessage = piiConverter.mask(message);
    assertThat(maskedMessage).isEqualTo(PIIConverter.getPiiMessageWarning());
  }

  @Test
  void testRemoveAddressLine() {
    String message = "AddressLine : abc";
    String maskedMessage = piiConverter.mask(message);
    assertThat(maskedMessage).isEqualTo(PIIConverter.getPiiMessageWarning());
  }

  @Test
  void testRemoveEmail() {
    String message = "Person[email=x@y.com]";
    String maskedMessage = piiConverter.mask(message);
    assertThat(maskedMessage).isEqualTo(PIIConverter.getPiiMessageWarning());
  }


  @Test
  void testCaseInsensitive() {
    String message = "MiDDlENamE = someName";
    String maskedMessage = piiConverter.mask(message);
    assertThat(maskedMessage).isEqualTo(PIIConverter.getPiiMessageWarning());
  }

  @Test
  void testDoNotRemoveHostname() {
    String message = "spring.cloud.client.hostname: ip-.eu-west-1.compute.internal";
    String maskedMessage = piiConverter.mask(message);
    assertThat(maskedMessage).isEqualTo(message);

  }

  @Test
  void testDoNotRemoveIPAddr() {
    String message = "spring.cloud.client.ip-address: 192.168.100.100 ";
    String maskedMessage = piiConverter.mask(message);
    assertThat(maskedMessage).isEqualTo(message);

  }


  @Test
  void testRemoveOtherInfo() {
    String message = "\"partyKey\": \"c4fa51fd-1b47-4288-bb21-58590b32d458\", \"givenName\": \"Jassedqis\", \"lastName\": \"Stathomihsc\", \"middleName\": \"Hankieml\", \"preferredName\": \"Jasonyzkj\", \"mobileNumber\": \"+447476746819\", \"email\": \"prsneon@10xbanking.supercoreauto\", \"address\": null, \"birthDate\": \"1990-10-10\", \"devices\": [], \"createdDate\": \"2020-10-15T08:25:06.888\", \"updatedDate\": \"2020-10-15T08:25:06.888\", \"status\": \"PROSPECT\", \"detailedStatus\": \"PROSPECT\", \"tenantKey\": \"794c2152-068f-4fcb-bd28-e2fe6d0854c6\", \"countryOfResidence\": \"GBR\", \"channelOfOrigination\": null}";
    String maskedMessage = piiConverter.mask(message);
    assertThat(maskedMessage).isEqualTo(PIIConverter.getPiiMessageWarning());

  }

  @Test
  void testAdditionalPattern() {
    String message = "myPIIInfoIsHere : hidden";
    ContextBase testContext = new ContextBase();
    testContext.setName("test");
    testContext.putProperty("extensionPIIPattern", "mypiiinfoishere");
    piiConverter.setContext(testContext);
    String maskedMessage = piiConverter.mask(message);
    assertThat(maskedMessage).isEqualTo(PIIConverter.getPiiMessageWarning());

  }


  @Test
  void testEmptyAdditionalPattern() {
    String message = "myPIIInfoIsHere : hidden";
    ContextBase testContext = new ContextBase();
    testContext.setName("test");
    testContext.putProperty("extensionPIIPattern", null);
    piiConverter.setContext(testContext);
    String maskedMessage = piiConverter.mask(message);
    assertThat(maskedMessage).isEqualTo(message);

  }

  @Test
  void testNullContext() {
    String message = "myPIIInfoIsHere : hidden";
    piiConverter.setContext(null);
    String maskedMessage = piiConverter.mask(message);
    assertThat(maskedMessage).isEqualTo(message);

  }

}

