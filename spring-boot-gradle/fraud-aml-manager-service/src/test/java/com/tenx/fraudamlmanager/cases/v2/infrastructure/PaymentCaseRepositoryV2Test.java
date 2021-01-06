package com.tenx.fraudamlmanager.cases.v2.infrastructure;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.domain.DomesticOutPaymentV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql({"/schema-hsqldb.sql"})
public class PaymentCaseRepositoryV2Test {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private PaymentCaseRepositoryV2 paymentCaseRepositoryV2;

  @BeforeEach
  public void init() {
    PaymentCaseEntityV2 paymentCaseEntityV2 = new PaymentCaseEntityV2();
    paymentCaseEntityV2.setTransactionId("txnId");
    paymentCaseEntityV2.setPaymentType(DomesticOutPaymentV2.class.getSimpleName());
    CaseV2 caseV2 = new CaseV2();
    caseV2.setCaseType("caseType");
    caseV2.add("key1", "val1");
    caseV2.setPrimaryPartyKey("pKey");
    caseV2.setSecondaryPartyKey("spKey");
    caseV2.setSubscriptionKey("sKey");
    paymentCaseEntityV2.setPaymentCase(caseV2);
    paymentCaseRepositoryV2.save(paymentCaseEntityV2);
  }

  @Test
  public void findByTransactionId() {
    PaymentCaseEntityV2 paymentCaseEntityV21 = paymentCaseRepositoryV2.findByTransactionId("txnId");
    assertThat(paymentCaseEntityV21.getPaymentType(),
        equalTo(DomesticOutPaymentV2.class.getSimpleName()));
    assertThat(paymentCaseEntityV21.getPaymentCase().getCaseType(), equalTo("caseType"));
    assertThat(paymentCaseEntityV21.getPaymentCase().getAttributes().get(0).getAttributeName(),
        equalTo("key1"));
    assertThat(paymentCaseEntityV21.getPaymentCase().getAttributes().get(0).getAttributeValue(),
        equalTo("val1"));
  }
}
