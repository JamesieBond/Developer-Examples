package com.tenx.fraudamlmanager.cases.infrastructure.governor;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.ALERT_TAG;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.ALERT_YES;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_REQUEST_FAILED;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_REQUEST_SUCCESS;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_CASEGOVERNOR_NAME;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_NAME_TAG;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.EMPTY_VALUE;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.GENERATED_BY_ACTION_TAG;
import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.PAYMENT_TYPE_NAME_TAG;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CaseMetrics {
  private static final String FAM_CASE_REQUEST_ACTION_VALUE = "fam_case_request";
  @Autowired
  private MeterRegistry registry;

  public void incrementCasesRequestsToCaseGovernorSuccess() {
    registry.counter(DOWNSTREAM_REQUEST_SUCCESS, Tags.of(PAYMENT_TYPE_NAME_TAG, EMPTY_VALUE,
        DOWNSTREAM_SERVICE_NAME_TAG, DOWNSTREAM_SERVICE_CASEGOVERNOR_NAME, GENERATED_BY_ACTION_TAG,
        FAM_CASE_REQUEST_ACTION_VALUE)).increment();
  }

  public void incrementCasesRequestsToCaseGovernorFailed() {

    registry.counter(DOWNSTREAM_REQUEST_FAILED, Tags.of(PAYMENT_TYPE_NAME_TAG, EMPTY_VALUE,
        DOWNSTREAM_SERVICE_NAME_TAG, DOWNSTREAM_SERVICE_CASEGOVERNOR_NAME, GENERATED_BY_ACTION_TAG,
        FAM_CASE_REQUEST_ACTION_VALUE, ALERT_TAG, ALERT_YES)).increment();
  }

  @PostConstruct
  public void initializeCounter() {
    registerCounterInstance(DOWNSTREAM_REQUEST_FAILED, PAYMENT_TYPE_NAME_TAG, EMPTY_VALUE,
        DOWNSTREAM_SERVICE_NAME_TAG, DOWNSTREAM_SERVICE_CASEGOVERNOR_NAME,
        ALERT_TAG, ALERT_YES, GENERATED_BY_ACTION_TAG, FAM_CASE_REQUEST_ACTION_VALUE);

    registerCounterInstance(DOWNSTREAM_REQUEST_SUCCESS, PAYMENT_TYPE_NAME_TAG, EMPTY_VALUE,
        DOWNSTREAM_SERVICE_NAME_TAG, DOWNSTREAM_SERVICE_CASEGOVERNOR_NAME, GENERATED_BY_ACTION_TAG,
        FAM_CASE_REQUEST_ACTION_VALUE);
  }

  private void registerCounterInstance(String name, String... tagNames) {
    Counter
        .builder(name).tags(tagNames)
        .register(registry);
  }

}
