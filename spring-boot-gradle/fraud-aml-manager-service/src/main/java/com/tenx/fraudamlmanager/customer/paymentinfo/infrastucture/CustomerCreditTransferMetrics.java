package com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerCreditTransferMetrics {

  private static final String FAM_FZM_REQUEST_TOTAL_COUNTER = "fam.fzm.customer.creditransfer.total";
  private static final String FAM_FZM_REQUEST_FAILED_COUNTER = "fam.fzm.customer.creditransfer.failed";
  private static final String FAM_FZM_REQUEST_CREDIT_TRANSFER_TYPE_TAG = "creditTransferType";
  private final MeterRegistry meterRegistry;

  public void incrementAcceptedTotalCounter() {
    meterRegistry.counter(FAM_FZM_REQUEST_TOTAL_COUNTER,
        Tags.of(FAM_FZM_REQUEST_CREDIT_TRANSFER_TYPE_TAG, CreditTransferType.ACCEPTED.toString())).increment();
  }

  public void incrementAcceptedFailedCounter() {
    meterRegistry.counter(FAM_FZM_REQUEST_FAILED_COUNTER,
        Tags.of(FAM_FZM_REQUEST_CREDIT_TRANSFER_TYPE_TAG, CreditTransferType.ACCEPTED.toString())).increment();
  }

  public void incrementRejectedTotalCounter() {
    meterRegistry.counter(FAM_FZM_REQUEST_TOTAL_COUNTER,
        Tags.of(FAM_FZM_REQUEST_CREDIT_TRANSFER_TYPE_TAG, CreditTransferType.REJECTED.toString())).increment();

  }

  public void incrementRejectedFailedCounter() {
    meterRegistry.counter(FAM_FZM_REQUEST_CREDIT_TRANSFER_TYPE_TAG,
        Tags.of(FAM_FZM_REQUEST_CREDIT_TRANSFER_TYPE_TAG, CreditTransferType.REJECTED.toString())).increment();
  }

  @PostConstruct
  public void initializeCounters() {
    registerCounterInstance(FAM_FZM_REQUEST_TOTAL_COUNTER, FAM_FZM_REQUEST_CREDIT_TRANSFER_TYPE_TAG,
        CreditTransferType.REJECTED.toString());
    registerCounterInstance(FAM_FZM_REQUEST_TOTAL_COUNTER, FAM_FZM_REQUEST_CREDIT_TRANSFER_TYPE_TAG,
        CreditTransferType.ACCEPTED.toString());

    registerCounterInstance(FAM_FZM_REQUEST_FAILED_COUNTER, FAM_FZM_REQUEST_CREDIT_TRANSFER_TYPE_TAG,
        CreditTransferType.ACCEPTED.toString());
    registerCounterInstance(FAM_FZM_REQUEST_FAILED_COUNTER, FAM_FZM_REQUEST_CREDIT_TRANSFER_TYPE_TAG,
        CreditTransferType.REJECTED.toString());
  }

  private void registerCounterInstance(String name, String... tagNames) {
    Counter
        .builder(name).tags(tagNames)
        .register(meterRegistry);
  }


  private enum CreditTransferType {
    ACCEPTED,
    REJECTED;
  }
}
