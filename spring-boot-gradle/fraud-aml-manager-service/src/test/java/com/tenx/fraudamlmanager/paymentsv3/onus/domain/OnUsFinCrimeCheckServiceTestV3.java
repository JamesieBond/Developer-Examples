package com.tenx.fraudamlmanager.paymentsv3.onus.domain;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentsDeviceProfileService;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.service.impl.FraudCheckResponseMetrics;
import com.tenx.fraudamlmanager.paymentsv3.api.AccountDetailsRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.api.BalanceBeforeRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.api.PaymentAmountRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.cases.domain.OnUsCaseServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure.OnUsPaymentV3;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Niall O'Connell
 */
@ExtendWith(SpringExtension.class)
public class OnUsFinCrimeCheckServiceTestV3 {

	@MockBean
	PaymentMetrics paymentMetrics;

	@MockBean
	FraudCheckResponseMetrics fraudCheckMetric;

	@MockBean
	OnUsCaseServiceV3 onUsCaseServiceV3;

	@MockBean
	PaymentsDeviceProfileService paymentsDeviceProfileService;

	private OnUsFinCrimeCheckServiceV3 onUsFinCrimeCheckServiceV3;

	@MockBean
	OnUsTransactionMonitoringConnector onUsTransactionMonitoringConnector;

	@BeforeEach
	public void beforeEach() {
		this.onUsFinCrimeCheckServiceV3 = new OnUsFinCrimeCheckServiceV3Impl(paymentMetrics, fraudCheckMetric, onUsCaseServiceV3,
						paymentsDeviceProfileService, onUsTransactionMonitoringConnector);
	}

	/**
	 * @throws TransactionMonitoringException Generic exception
	 */
	@Test
	public void onUsPaymentPassedMethodV3() throws TransactionMonitoringException {
		OnUsPaymentV3 onUsPaymentV3 = new OnUsPaymentV3(
						new AccountDetailsRequestV3("Test", "Test"),
						"Test",
						new AccountDetailsRequestV3("Test", "Test"),
						"Test",
						new PaymentAmountRequestV3("Test", 2, "Test", 2),
						new BalanceBeforeRequestV3("GBP", 500.00, "GBP", 500.00),
						"Test",
						"Test",
						"Test",
						new Date(), new Date(),
						"Test",
						"Test",
						"Test",
						new ArrayList<>(),
						true,
						new HashMap<>()
		);

		FraudCheckV3 fraudCheckV3 = new FraudCheckV3(
				FraudAMLSanctionsCheckResponseCodeV3.passed);

		given(onUsTransactionMonitoringConnector.checkFinCrimeV3(onUsPaymentV3))
				.willReturn(fraudCheckV3);

		FraudCheckV3 fraudCheckResponseV3 = onUsFinCrimeCheckServiceV3
				.checkFinCrimeV3(onUsPaymentV3, "deviceKeyId2");
		// Passed status should neither save payment nor create case
		Mockito.verify(onUsCaseServiceV3, times(1)).processCaseForOnUs(eq(FraudAMLSanctionsCheckResponseCodeV3.passed), eq(onUsPaymentV3));

		Mockito.verify(onUsTransactionMonitoringConnector, times(1)).checkFinCrimeV3(onUsPaymentV3);
		Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.ON_US);
		Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
				fraudCheckMetric.ONUS_FRAUDCHECK_TAG, "passed");
		Assert.assertEquals(FraudAMLSanctionsCheckResponseCodeV3.passed, fraudCheckV3.getStatus());
	}

	/**
	 * @throws TransactionMonitoringException Generic exception
	 */
	@Test
	public void checkOnUsReferredMethod() throws TransactionMonitoringException {
		OnUsPaymentV3 onUsPaymentV3 = new OnUsPaymentV3();
		FraudCheckV3 fraudCheckV3 = new FraudCheckV3(
				FraudAMLSanctionsCheckResponseCodeV3.referred);

		given(onUsTransactionMonitoringConnector.checkFinCrimeV3(onUsPaymentV3))
				.willReturn(fraudCheckV3);
		onUsFinCrimeCheckServiceV3.checkFinCrimeV3(onUsPaymentV3, "deviceKeyId2");

		// Referred status should not save payment but create case
		Mockito.verify(onUsCaseServiceV3, times(1)).processCaseForOnUs(eq(FraudAMLSanctionsCheckResponseCodeV3.referred), eq(onUsPaymentV3));

		Mockito.verify(onUsTransactionMonitoringConnector, times(1)).checkFinCrimeV3(onUsPaymentV3);
		Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.ON_US);
		Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
				fraudCheckMetric.ONUS_FRAUDCHECK_TAG, "referred");
	}

	/**
	 * @throws TransactionMonitoringException Generic exception
	 */
	@Test
	public void checkOnUsPendingMethod() throws TransactionMonitoringException {
		OnUsPaymentV3 onUsPaymentV3 = new OnUsPaymentV3();
		FraudCheckV3 fraudCheckV3 = new FraudCheckV3(
				FraudAMLSanctionsCheckResponseCodeV3.pending);

		given(onUsTransactionMonitoringConnector.checkFinCrimeV3(onUsPaymentV3))
				.willReturn(fraudCheckV3);

		onUsFinCrimeCheckServiceV3.checkFinCrimeV3(onUsPaymentV3, "deviceKeyId2");

		// Pending status should save payment but do not create case
		Mockito.verify(onUsCaseServiceV3, times(1)).processCaseForOnUs(eq(FraudAMLSanctionsCheckResponseCodeV3.pending), eq(onUsPaymentV3));

		Mockito.verify(onUsTransactionMonitoringConnector, times(1)).checkFinCrimeV3(onUsPaymentV3);
		Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.ON_US);
		Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
				fraudCheckMetric.ONUS_FRAUDCHECK_TAG, "pending");
	}

}

