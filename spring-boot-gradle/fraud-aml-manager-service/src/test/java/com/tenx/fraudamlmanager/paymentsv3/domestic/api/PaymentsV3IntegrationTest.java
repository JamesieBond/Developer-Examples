package com.tenx.fraudamlmanager.paymentsv3.domestic.api;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.tenx.fraudamlmanager.SpringBootTestBase;
import com.tenx.fraudamlmanager.deviceprofile.infrastructure.DeviceProfileEntity;
import com.tenx.fraudamlmanager.deviceprofile.infrastructure.DeviceProfilingEventRepository;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.paymentsv3.api.AccountDetailsRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.api.BalanceBeforeRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.api.FraudCheckResponseV3;
import com.tenx.fraudamlmanager.paymentsv3.api.PaymentAmountRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.FraudAMLSanctionsCheckResponseV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.api.OnUsPaymentRequestV3;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public class PaymentsV3IntegrationTest extends SpringBootTestBase {

    private static WireMockServer wireMockServer;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private TransactionMonitoringClient transactionMonitoringClient;

    @MockBean
    private DeviceProfilingEventRepository deviceProfilingEventRepository;

    private static Stream<Arguments> stubbedURLs() {
        DomesticOutPaymentRequestV3 domesticOutPaymentRequestV3 = new DomesticOutPaymentRequestV3();
        domesticOutPaymentRequestV3.setCreditorAccountDetails(new AccountDetailsRequestV3("Test", "Test"));
        domesticOutPaymentRequestV3.setCreditorName("Test");
        domesticOutPaymentRequestV3.setDebtorAccountDetails(new AccountDetailsRequestV3("Test", "Test"));
        domesticOutPaymentRequestV3.setDebtorName("test");
        domesticOutPaymentRequestV3.setDebtorPartyKey("debtorPartyKey");
        domesticOutPaymentRequestV3.setAmount(new PaymentAmountRequestV3("Test", 200, "Test", 200));
        domesticOutPaymentRequestV3.setBalanceBefore(new BalanceBeforeRequestV3("GBP", 500.00, "GBP", 500.00));
        domesticOutPaymentRequestV3.setTransactionId("Testing1234");
        domesticOutPaymentRequestV3.setTransactionDate(new Date());
        domesticOutPaymentRequestV3.setMessageDate(new Date());
        domesticOutPaymentRequestV3.setTransactionStatus("test");
        domesticOutPaymentRequestV3.setTransactionReference("test");
        domesticOutPaymentRequestV3.setTransactionNotes("test");
        domesticOutPaymentRequestV3.setTransactionTags(new ArrayList<>());
        domesticOutPaymentRequestV3.setExistingPayee(Boolean.TRUE);

        OnUsPaymentRequestV3 onUsPaymentRequestV3 = new OnUsPaymentRequestV3(
                new AccountDetailsRequestV3("Test", "Test"),
                "Test",
                new AccountDetailsRequestV3("Test", "Test"),
                "Test",
                new PaymentAmountRequestV3("Test", 2, "Test", 2),
                new BalanceBeforeRequestV3("GBP", 500.00, "GBP", 500.00),
                "Test",
                "partyKey123",
                "Test",
                new Date(), new Date(),
                "Test",
                "Test",
                "Test",
                new ArrayList<>(),
                true
        );

        HashMap threatmetrixData = new HashMap();
        threatmetrixData.put("device_key_id", "deviceId123");
        threatmetrixData.put("device_id", "deviceId123");
        DeviceProfileEntity deviceProfileEntity = new DeviceProfileEntity();
        deviceProfileEntity.setDeviceProfile(threatmetrixData);

        FraudAMLSanctionsCheckResponseV3 fraudAMLSanctionsCheckResponse = new FraudAMLSanctionsCheckResponseV3(
            FraudAMLSanctionsCheckResponseCodeV3.passed);

        FraudCheckResponseV3 fraudCheckResponse = new FraudCheckResponseV3(FraudAMLSanctionsCheckResponseCodeV3.passed);

        return Stream.of(
            Arguments.of("/v3/payments/domesticPaymentOutboundFinCrimeCheck",
                "/v3/payments/domesticPaymentOutboundFinCrimeCheck", domesticOutPaymentRequestV3,
                deviceProfileEntity, fraudAMLSanctionsCheckResponse),
            Arguments.of("/v3/payments/onUsFinCrimeCheck", "/v3/payments/onUsFinCrimeCheck", onUsPaymentRequestV3,
                deviceProfileEntity, fraudAMLSanctionsCheckResponse)
        );
    }

    @BeforeAll
    public static void beforeEach() {
        setupWiremock();
    }

    @AfterAll
    public static void teardown() {
        wireMockServer.stop();
    }

    private static void setupWiremock() {
        wireMockServer = new WireMockServer(wireMockConfig().port(1234));
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    private void stubTMAService(String stubUrl, Object fraudAMLSanctionsCheckResponse) throws JsonProcessingException {
        stubFor(post(urlEqualTo(stubUrl))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(objectMapper.writeValueAsString(fraudAMLSanctionsCheckResponse))));
    }

    @ParameterizedTest
    @MethodSource("stubbedURLs")
    public void FPSpaymentsE2EIntegrationTest(String newStubbedURL, String stubbedURL, Object payload,
        DeviceProfileEntity deviceProfileEntity, Object fraudAMLSanctionsCheckResponse) throws Exception {
        stubTMAService(newStubbedURL, fraudAMLSanctionsCheckResponse);

        given(deviceProfilingEventRepository.findByPartyKeyAndDeviceKeyId("partyKey123", "deviceKeyId123"))
            .willReturn(deviceProfileEntity);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(stubbedURL)
            .content(objectMapper.writeValueAsString(payload))
            .contentType(MediaType.APPLICATION_JSON)
            .header("deviceKeyId", "deviceKeyId123")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath(".status", hasItem("passed")));
    }

}
