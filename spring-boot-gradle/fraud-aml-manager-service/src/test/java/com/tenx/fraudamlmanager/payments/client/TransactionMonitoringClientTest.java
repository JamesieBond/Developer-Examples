package com.tenx.fraudamlmanager.payments.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.tenx.fraudamlmanager.SpringBootTestBase;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TransactionMonitoringClientTest extends SpringBootTestBase {

    @Autowired
    private TransactionMonitoringClient transactionMonitoringClient;

    private static String TEST_URL = "/v1/payments/onUsFinCrimeCheck";
    private static WireMockServer wireMockServer;

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

    @Test
    public void checkErrorDecoderHandles400Status() {
        stubFor(post(TEST_URL)
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("Bad Request")));
        OnUsPayment onUsPayment = new OnUsPayment();
        TransactionMonitoringException transactionMonitoringException = assertThrows(TransactionMonitoringException.class, () ->
                transactionMonitoringClient.checkFinCrime(onUsPayment));
        assertThat(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), is(equalTo(400)));
        assertThat(transactionMonitoringException.getErrorDetails().getMessage(), not(isEmptyString()));
    }

    @Test
    public void checkErrorDecoderHandles500Status() {
        stubFor(post(TEST_URL)
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));
        OnUsPayment onUsPayment = new OnUsPayment();
        TransactionMonitoringException transactionMonitoringException = assertThrows(TransactionMonitoringException.class, () ->
                transactionMonitoringClient.checkFinCrime(onUsPayment));
        assertThat(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), is(equalTo(500)));
        assertThat(transactionMonitoringException.getErrorDetails().getMessage(), not(isEmptyString()));
    }
}