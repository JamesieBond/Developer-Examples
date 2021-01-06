package com.tenx.fraudamlmanager.payments.service.impl;

import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditBacsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitBacsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.domain.DomesticOutPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FraudCheckResponseMetrics {

  public final String ONUS_FRAUDCHECK_TAG = "onUsFinCrimeCheck";
  public final String OUTBOUND_FRAUDCHECK_TAG = "domesticPaymentOutboundFinCrimeCheck";
  public final String OUTBOUND_RETURN_FRAUDCHECK_TAG = "domesticPaymentOutboundReturnFinCrimeCheck";
  public final String INBOUND_FRAUDCHECK_TAG = "domesticPaymentInboundFinCrimeCheck";
  public final String DIRECT_DEBIT_FRAUDCHECK_TAG = "directdebitFinCrimeCheck";
  public final String DIRECT_CREDIT_FRAUDCHECK_TAG = "directcreditFinCrimeCheck";
  public final String TM_PAYMENT_TAG = "tmPaymentFinCrimeCheck";
  private final String statusTagName = "status";
  private final String typeTagName = "type";

  private static final String PAYMENT_FRAUD_CHECK =
      "payment.fraudcheck.status.counter";

  public String mapPaymentType(String v2PaymentType){
    if(v2PaymentType.equals(OnUsPaymentV2.class.getSimpleName()))
      return ONUS_FRAUDCHECK_TAG;
    else if(v2PaymentType.equals(DomesticInPaymentV2.class.getSimpleName()))
      return INBOUND_FRAUDCHECK_TAG;
    else if (v2PaymentType.equals(DomesticOutPaymentV2.class.getSimpleName()))
      return OUTBOUND_FRAUDCHECK_TAG;
    else if (v2PaymentType.equals(DomesticOutReturnPaymentV2.class.getSimpleName()))
      return OUTBOUND_RETURN_FRAUDCHECK_TAG;
    else if (v2PaymentType.equals(DirectDebitBacsPaymentV2.class.getSimpleName()))
      return DIRECT_DEBIT_FRAUDCHECK_TAG;
    else if (v2PaymentType.equals(DirectCreditBacsPaymentV2.class.getSimpleName()))
      return DIRECT_CREDIT_FRAUDCHECK_TAG;
    else
      return "paymentTypeNotFound";
  }

  private Counter fraudCheckCounter = null;

  @Autowired
  private MeterRegistry registry;

  public void incrementFraudCheck(String typeValue,String statusValue) {
    registry.counter(PAYMENT_FRAUD_CHECK, Tags.of(typeTagName, typeValue, statusTagName, statusValue)).increment();
  }

  @PostConstruct
  public void initializeCounter() {
    fraudCheckCounter = getCounterInstance(PAYMENT_FRAUD_CHECK);
  }


  private Counter getCounterInstance(String name) {
    return Counter
        .builder(name)
        .tag(typeTagName, ONUS_FRAUDCHECK_TAG)
        .tag(typeTagName, INBOUND_FRAUDCHECK_TAG)
        .tag(typeTagName, OUTBOUND_FRAUDCHECK_TAG)
        .tag(typeTagName, OUTBOUND_RETURN_FRAUDCHECK_TAG)
        .tag(typeTagName, DIRECT_DEBIT_FRAUDCHECK_TAG)
        .tag(typeTagName, DIRECT_CREDIT_FRAUDCHECK_TAG)
        .tag(typeTagName, TM_PAYMENT_TAG)
        .tag(statusTagName, "blocked")
        .tag(statusTagName, "rejected")
        .tag(statusTagName, "passed")
        .tag(statusTagName, "referred")
        .tag(statusTagName, "cancelled")
        .tag(statusTagName, "pending")
        .register(registry);
  }
}
