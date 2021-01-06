package com.tenx.fraudamlmanager.payments.service;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.cases.PaymentCaseServiceV1;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.model.api.DirectCreditPayment;
import com.tenx.fraudamlmanager.payments.model.api.DirectDebitPayment;
import com.tenx.fraudamlmanager.payments.model.api.FpsInboundPayment;
import com.tenx.fraudamlmanager.payments.model.api.FpsOutboundPayment;
import com.tenx.fraudamlmanager.payments.model.api.FraudCheckResponse;
import com.tenx.fraudamlmanager.payments.model.api.OnUsPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DirectCreditBacsPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DirectDebitBacsPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticInPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticOutPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.ExternalCaseDetails;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.FraudAMLSanctionsCheckResponse;
import com.tenx.fraudamlmanager.payments.service.impl.FinCrimeCheckServiceImpl;
import com.tenx.fraudamlmanager.payments.service.impl.FraudCheckResponseMetrics;
import com.tenx.fraudamlmanager.payments.service.impl.PaymentMappingService;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author James Spencer
 */
@ExtendWith(SpringExtension.class)
public class FinCrimeCheckServiceTest {

  private FinCrimeCheckService finCrimeCheckService;

  @MockBean
  TransactionMonitoringClient transactionMonitoringClient;

  @MockBean
  PaymentMappingService paymentMappingService;

  @MockBean
  PaymentMetrics paymentMetrics;

  @MockBean
  FraudCheckResponseMetrics fraudCheckMetric;

  @MockBean
  PaymentCaseServiceV1 paymentCaseServiceV1;

  @BeforeEach
  public void beforeEach() {
    this.finCrimeCheckService = new FinCrimeCheckServiceImpl(paymentMetrics, fraudCheckMetric,
        transactionMonitoringClient, paymentMappingService, paymentCaseServiceV1);
  }

  /**
   * @throws TransactionMonitoringException Generic exception
   */
  @Test
  public void checkDomesticInPaymentMethod() throws TransactionMonitoringException {

    FpsInboundPayment fpsInboundPayment = new FpsInboundPayment();
    DomesticInPayment domesticInPayment = new DomesticInPayment();
    FraudAMLSanctionsCheckResponse fraudAMLSanctionsCheckResponse =
        new FraudAMLSanctionsCheckResponse("id123567", "Passed", new ArrayList<ExternalCaseDetails>());
    given(paymentMappingService.mapToFpsInboundPayment(fpsInboundPayment)).willReturn(domesticInPayment);
    given(transactionMonitoringClient.checkFinCrime(domesticInPayment)).willReturn(fraudAMLSanctionsCheckResponse);
    given(paymentMappingService.mapToOldFraudCheckResponse(fraudAMLSanctionsCheckResponse))
        .willReturn(new FraudCheckResponse(true, "Passed"));
    finCrimeCheckService.checkFinCrime(fpsInboundPayment);

    Mockito.verify(transactionMonitoringClient, times(1)).checkFinCrime(domesticInPayment);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_IN);
    Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
        fraudCheckMetric.INBOUND_FRAUDCHECK_TAG, "passed");
  }

  /**
   * @throws TransactionMonitoringException Generic exception
   */
  @Test
  public void checkDomesticOutPaymentPassedMethod() throws TransactionMonitoringException, PaymentCaseException {
    FpsOutboundPayment fpsOutboundPayment = new FpsOutboundPayment();
    DomesticOutPayment domesticOutPayment = new DomesticOutPayment();
    FraudAMLSanctionsCheckResponse fraudAMLSanctionsCheckResponse =
        new FraudAMLSanctionsCheckResponse("id123567", "Passed", new ArrayList<ExternalCaseDetails>());

    given(paymentMappingService.mapToFpsOutboundPayment(fpsOutboundPayment)).willReturn(domesticOutPayment);
    given(transactionMonitoringClient.checkFinCrime(domesticOutPayment)).willReturn(fraudAMLSanctionsCheckResponse);
    given(paymentMappingService.mapToOldFraudCheckResponse(fraudAMLSanctionsCheckResponse)).
        willReturn(new FraudCheckResponse(true, "Passed"));
    finCrimeCheckService.checkFinCrime(fpsOutboundPayment);

    Mockito.verify(transactionMonitoringClient, times(1)).checkFinCrime(domesticOutPayment);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);
    Mockito.verify(paymentCaseServiceV1, times(0)).createBlockedPaymentCase(domesticOutPayment);
    Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
        fraudCheckMetric.OUTBOUND_FRAUDCHECK_TAG, "passed");
  }

  /**
   * @throws TransactionMonitoringException Generic exception
   */
  @Test
  public void checkDomesticOutPaymentReferredMethod() throws TransactionMonitoringException, PaymentCaseException {
    FpsOutboundPayment fpsOutboundPayment = new FpsOutboundPayment();
    DomesticOutPayment domesticOutPayment = new DomesticOutPayment();
    FraudAMLSanctionsCheckResponse fraudAMLSanctionsCheckResponse =
        new FraudAMLSanctionsCheckResponse("id123567", "Referred", new ArrayList<ExternalCaseDetails>());
    doNothing().when(paymentCaseServiceV1).createBlockedPaymentCase(domesticOutPayment);

    given(paymentMappingService.mapToFpsOutboundPayment(fpsOutboundPayment)).willReturn(domesticOutPayment);
    given(transactionMonitoringClient.checkFinCrime(domesticOutPayment)).willReturn(fraudAMLSanctionsCheckResponse);
    given(paymentMappingService.mapToOldFraudCheckResponse(fraudAMLSanctionsCheckResponse))
        .willReturn(new FraudCheckResponse(true, "Referred"));
    finCrimeCheckService.checkFinCrime(fpsOutboundPayment);

    Mockito.verify(transactionMonitoringClient, times(1)).checkFinCrime(domesticOutPayment);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);
    Mockito.verify(paymentCaseServiceV1, times(1)).createBlockedPaymentCase(domesticOutPayment);
    Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
        fraudCheckMetric.OUTBOUND_FRAUDCHECK_TAG, "referred");
  }

  /**
   * @throws TransactionMonitoringException Generic exception
   */
  @Test
  public void checkDirectCreditBacsMethod() throws TransactionMonitoringException {

    DirectCreditPayment directCreditPayment = new DirectCreditPayment();
    DirectCreditBacsPayment directCreditBacsPayment = new DirectCreditBacsPayment();
    FraudAMLSanctionsCheckResponse fraudAMLSanctionsCheckResponse = new FraudAMLSanctionsCheckResponse("id123567", "Passed", new ArrayList<ExternalCaseDetails>());

    given(paymentMappingService.mapToDirectCreditPayment(directCreditPayment)).willReturn(directCreditBacsPayment);
    given(transactionMonitoringClient.checkFinCrimeDirectCredit(directCreditBacsPayment)).willReturn(fraudAMLSanctionsCheckResponse);
    given(paymentMappingService.mapToOldFraudCheckResponse(fraudAMLSanctionsCheckResponse)).willReturn(new FraudCheckResponse(true, "Passed"));
    finCrimeCheckService.checkFinCrime(directCreditPayment);

    Mockito.verify(transactionMonitoringClient, times(1)).checkFinCrimeDirectCredit(directCreditBacsPayment);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_CREDIT);
    Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
        fraudCheckMetric.DIRECT_CREDIT_FRAUDCHECK_TAG, "passed");
  }

  /**
   * @throws TransactionMonitoringException Generic exception
   */
  @Test
  public void checkDirectDebitBacsMethod() throws TransactionMonitoringException {

    DirectDebitBacsPayment directDebitBacsPayment = new DirectDebitBacsPayment();
    DirectDebitPayment directDebitPayment = new DirectDebitPayment();
    FraudAMLSanctionsCheckResponse fraudAMLSanctionsCheckResponse = new FraudAMLSanctionsCheckResponse("id123567", "Passed", new ArrayList<ExternalCaseDetails>());

    given(paymentMappingService.mapToDiectDebitPayment(directDebitPayment)).willReturn(directDebitBacsPayment);
    given(transactionMonitoringClient.checkFinCrimeDirectDebit(directDebitBacsPayment)).willReturn(fraudAMLSanctionsCheckResponse);
    given(paymentMappingService.mapToOldFraudCheckResponse(fraudAMLSanctionsCheckResponse)).willReturn(new FraudCheckResponse(true, "Passed"));
    finCrimeCheckService.checkFinCrime(directDebitPayment);

    Mockito.verify(transactionMonitoringClient, times(1)).checkFinCrimeDirectDebit(directDebitBacsPayment);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_DEBIT);
    Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
        fraudCheckMetric.DIRECT_DEBIT_FRAUDCHECK_TAG, "passed");
  }


  /**
   * @throws TransactionMonitoringException Generic exception
   */
  @Test
  public void checkOnUsPaymentPassedMethod() throws TransactionMonitoringException, PaymentCaseException {

    OnUsPayment onUsPaymentOld = new OnUsPayment();
    com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment onUsPayment = new com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment();
    FraudAMLSanctionsCheckResponse fraudAMLSanctionsCheckResponse = new FraudAMLSanctionsCheckResponse("id123",
        "Passed", new ArrayList<ExternalCaseDetails>());

    given(paymentMappingService.mapOnUsPayment(onUsPaymentOld)).willReturn(onUsPayment);
    given(transactionMonitoringClient.checkFinCrime(onUsPayment)).willReturn(fraudAMLSanctionsCheckResponse);
    given(paymentMappingService.mapToOldFraudCheckResponse(fraudAMLSanctionsCheckResponse))
        .willReturn(new FraudCheckResponse(true, "Passed"));
    finCrimeCheckService.checkFinCrime(onUsPaymentOld);

    Mockito.verify(transactionMonitoringClient, times(1)).checkFinCrime(onUsPayment);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.ON_US);
    Mockito.verify(paymentCaseServiceV1, times(0)).createBlockedPaymentCase(onUsPayment);
    Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
        fraudCheckMetric.ONUS_FRAUDCHECK_TAG, "passed");

  }

  /**
   * @throws TransactionMonitoringException Generic exception
   */
  @Test
  public void checkOnUsPaymentReferredMethod() throws TransactionMonitoringException, PaymentCaseException {

    OnUsPayment onUsPaymentOld = new OnUsPayment();
    com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment onUsPayment = new com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment();
    FraudAMLSanctionsCheckResponse fraudAMLSanctionsCheckResponse = new FraudAMLSanctionsCheckResponse("id123",
        "Referred", new ArrayList<ExternalCaseDetails>());
    doNothing().when(paymentCaseServiceV1).createBlockedPaymentCase(onUsPayment);

    given(paymentMappingService.mapOnUsPayment(onUsPaymentOld)).willReturn(onUsPayment);
    given(transactionMonitoringClient.checkFinCrime(onUsPayment)).willReturn(fraudAMLSanctionsCheckResponse);
    given(paymentMappingService.mapToOldFraudCheckResponse(fraudAMLSanctionsCheckResponse))
        .willReturn(new FraudCheckResponse(true, "Referred"));
    finCrimeCheckService.checkFinCrime(onUsPaymentOld);

    Mockito.verify(transactionMonitoringClient, times(1)).checkFinCrime(onUsPayment);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.ON_US);
    Mockito.verify(paymentCaseServiceV1, times(1)).createBlockedPaymentCase(onUsPayment);
    Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
        fraudCheckMetric.ONUS_FRAUDCHECK_TAG, "referred");
  }
}



