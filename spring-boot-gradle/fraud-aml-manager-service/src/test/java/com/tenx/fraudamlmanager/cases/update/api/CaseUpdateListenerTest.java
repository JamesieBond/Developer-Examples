package com.tenx.fraudamlmanager.cases.update.api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.dub.casegovernor.event.v1.CaseEventV2;
import com.tenx.fraudamlmanager.cases.update.domain.Attribute;
import com.tenx.fraudamlmanager.cases.update.domain.CaseUpdateService;
import com.tenx.fraudamlmanager.cases.update.domain.PaymentCaseUpdate;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseStatus;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CaseUpdateListenerTest {

  @MockBean
  private CaseUpdateService caseUpdateService;

  @Mock
  private Acknowledgment acknowledgment;

  private CaseUpdateListener caseUpdateListener;

  private static Stream<Arguments> payloads() {
    com.tenx.dub.casegovernor.event.v1.Attribute attribute = com.tenx.dub.casegovernor.event.v1.Attribute.newBuilder()
        .setAttributeName("transactionId")
        .setAttributeValue("transactionId")
        .build();

    List<com.tenx.dub.casegovernor.event.v1.Attribute> attributes = new ArrayList<com.tenx.dub.casegovernor.event.v1.Attribute>();
    attributes.add(attribute);
    CaseEventV2 caseEventV2 =
        CaseEventV2.newBuilder()
            .setCaseType(CaseV2.CaseType.FRAUD_EXCEPTION.name())
            .setStatus(CaseStatus.CLOSED.name())
            .setTenxCaseId("caseId")
            .setOutcome("Passed")
            .setPartyKey("partyKey")
            .setCreatedTimeStamp("test")
            .setUpdatedTimeStamp("test")
            .setAttributes(attributes)
            .build();

    Attribute attributeTransactionId = new Attribute(
        "transactionId",
        "transactionId"
    );
    PaymentCaseUpdate caseEventData = new PaymentCaseUpdate(
        "Passed",
        CaseStatus.CLOSED.name(),
        "FRAUD_EXCEPTION",
        "partyKey"
    );
    caseEventData.getAttributes().add(attributeTransactionId);

    CaseEventV2 caseEventV2NoAttribute =
        CaseEventV2.newBuilder()
            .setCaseType(CaseV2.CaseType.FRAUD_EXCEPTION.name())
            .setStatus(CaseStatus.CLOSED.name())
            .setTenxCaseId("caseId")
            .setOutcome("Passed")
            .setPartyKey("partyKey")
            .setCreatedTimeStamp("test")
            .setUpdatedTimeStamp("test")
            .setAttributes(new ArrayList<>())
            .build();

    PaymentCaseUpdate caseEventDataNoAttributes = new PaymentCaseUpdate(
        "Passed",
        CaseStatus.CLOSED.name(),
        "FRAUD_EXCEPTION",
        "partyKey"
    );

    return Stream.of(
        Arguments.of(caseEventV2, caseEventData),
        Arguments.arguments(caseEventV2NoAttribute, caseEventDataNoAttributes));
  }

  @BeforeEach
  void beforeEach() {
    caseUpdateListener = new CaseUpdateListener(caseUpdateService);
  }

  @ParameterizedTest
  @MethodSource("payloads")
  void testProcessFinCrimeCheckCaseResult(CaseEventV2 caseEventV2, PaymentCaseUpdate caseEventData)
      throws FinCrimeCheckResultException {

    ConsumerRecord<String, CaseEventV2> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", caseEventV2);

    caseUpdateListener.handleCaseUpdate(consumerRecord, acknowledgment);
    verify(caseUpdateService, times(1))
        .checkForUpdateFinCrimeCheck(eq(caseEventData));

  }

  @ParameterizedTest
  @MethodSource("payloads")
  void testProcessFinCrimeCheckCaseResultThrowingNPEException(CaseEventV2 caseEventV2,
      PaymentCaseUpdate caseEventData)
      throws FinCrimeCheckResultException {

    ConsumerRecord<String, CaseEventV2> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", caseEventV2);

    doThrow(FinCrimeCheckResultException.class).when(caseUpdateService).checkForUpdateFinCrimeCheck(any());

    assertDoesNotThrow(() -> caseUpdateListener.handleCaseUpdate(consumerRecord, acknowledgment));

    verify(caseUpdateService, times(1))
        .checkForUpdateFinCrimeCheck(eq(caseEventData));

    verify(acknowledgment, times(0)).acknowledge();
  }


}
