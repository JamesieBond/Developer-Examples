package com.tenx.fraudamlmanager.domain;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.ALERT_TAG;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.ALERT_YES;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_REQUEST_FAILED;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_REQUEST_SUCCESS;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_NAME_TAG;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.EMPTY_VALUE;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.FINCRIMECHECKRESULT_ACTION_VALUE;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.GENERATED_BY_ACTION_TAG;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.PAYMENT_ACTION_VALUE;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.PAYMENT_TYPE_NAME_TAG;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author pbodea
 */
@Component
public class PaymentMetrics {


  public static final String APP_NAME = "fam.";
  public static final String PAYMENTS_RECEIVED = "payments.received";
  public static final String PAYMENTS_RETURNED = "payments.returned";
  public static final String PAYMENTS_FPS_FRAUDCHECK_PUBLISHED = "payments.fps.fraudcheck.published";

  @Autowired
  private MeterRegistry registry;

  @PostConstruct
  public void initializeCounter() {
    initializeCounterType(PAYMENTS_RECEIVED);
    initializeFailTransactionMonitoringCounterType();
    initializeSuccessTransactionMonitoringCounterType();

    registerCounterInstance(APP_NAME + PAYMENTS_RETURNED, PAYMENTS_RETURNED, PaymentMetricsType.DOMESTIC_OUT.toString());

    registerCounterInstance(DOWNSTREAM_REQUEST_SUCCESS, PAYMENT_TYPE_NAME_TAG, EMPTY_VALUE,
        DOWNSTREAM_SERVICE_NAME_TAG, DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, GENERATED_BY_ACTION_TAG, FINCRIMECHECKRESULT_ACTION_VALUE);

    registerCounterInstance(DOWNSTREAM_REQUEST_FAILED, PAYMENT_TYPE_NAME_TAG, EMPTY_VALUE,
        DOWNSTREAM_SERVICE_NAME_TAG, DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, GENERATED_BY_ACTION_TAG, FINCRIMECHECKRESULT_ACTION_VALUE,
        ALERT_TAG, ALERT_YES);

    registerCounterInstance(APP_NAME + PAYMENTS_FPS_FRAUDCHECK_PUBLISHED);
  }


  private void initializeCounterType(String metric) {
    for (PaymentMetricsType metricType : PaymentMetricsType.values()) {
      registerCounterInstance(APP_NAME + metric, metric, metricType.toString());
    }
  }

  private void initializeFailTransactionMonitoringCounterType() {
    for (PaymentMetricsType metricType : PaymentMetricsType.values()) {
      registerCounterInstance( DOWNSTREAM_REQUEST_FAILED, PAYMENT_TYPE_NAME_TAG, metricType.toString(),
          DOWNSTREAM_SERVICE_NAME_TAG, DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME,
          ALERT_TAG, ALERT_YES, GENERATED_BY_ACTION_TAG, PAYMENT_ACTION_VALUE);
    }
  }

  private void initializeSuccessTransactionMonitoringCounterType() {
    for (PaymentMetricsType metricType : PaymentMetricsType.values()) {
      registerCounterInstance(DOWNSTREAM_REQUEST_SUCCESS, PAYMENT_TYPE_NAME_TAG, metricType.toString(),
          DOWNSTREAM_SERVICE_NAME_TAG, DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, GENERATED_BY_ACTION_TAG,
          PAYMENT_ACTION_VALUE);
    }
  }

  private void registerCounterInstance(String name, String... tagNames) {
    Counter
        .builder(name)
        .tags(tagNames)
        .register(registry);
  }

  public void incrementCounter(String metric) {
    registry.counter(APP_NAME + metric).increment();
  }

  public void incrementDownStreamFailPayment(String downStreamService, PaymentMetricsType paymentMetricsType){
    registry.counter(DOWNSTREAM_REQUEST_FAILED, Tags.of(DOWNSTREAM_SERVICE_NAME_TAG, downStreamService,
        ALERT_TAG, ALERT_YES, PAYMENT_TYPE_NAME_TAG, paymentMetricsType.toString(), GENERATED_BY_ACTION_TAG, PAYMENT_ACTION_VALUE)).
        increment();
  }

  public void incrementDownStreamSuccessPayment(String downStreamService, PaymentMetricsType paymentMetricsType){
    registry.counter(DOWNSTREAM_REQUEST_SUCCESS, Tags.of(DOWNSTREAM_SERVICE_NAME_TAG, downStreamService,
        PAYMENT_TYPE_NAME_TAG, paymentMetricsType.toString(), GENERATED_BY_ACTION_TAG, PAYMENT_ACTION_VALUE)).
        increment();
  }

  public void incrementDownStreamFailFincrimeCheckResult(String downStreamService){
    registry.counter(DOWNSTREAM_REQUEST_FAILED, Tags.of(DOWNSTREAM_SERVICE_NAME_TAG, downStreamService,
        ALERT_TAG, ALERT_YES, PAYMENT_TYPE_NAME_TAG, EMPTY_VALUE, GENERATED_BY_ACTION_TAG, PAYMENT_ACTION_VALUE)).
        increment();
  }

  public void incrementDownStreamSuccessFincrimeCheckResult(String downStreamService){
    registry.counter(DOWNSTREAM_REQUEST_SUCCESS, Tags.of(DOWNSTREAM_SERVICE_NAME_TAG, downStreamService,
        PAYMENT_TYPE_NAME_TAG, EMPTY_VALUE, GENERATED_BY_ACTION_TAG, FINCRIMECHECKRESULT_ACTION_VALUE)).
        increment();
  }

  public void incrementCounterTag(String metric, String tag) {
    registry.counter(APP_NAME + metric,
        Tags.of(metric, String.valueOf(tag))).increment();
  }
}
