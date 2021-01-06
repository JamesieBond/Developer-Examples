package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraud.payments.fps.FPSFraudCheckResponse;
import com.tenx.fraudamlmanager.KafkaTestBase;
import com.tenx.fraudamlmanager.cases.domain.CaseProcessingService;
import com.tenx.fraudamlmanager.cases.domain.FinCrimeCheckCase;
import com.tenx.fraudamlmanager.cases.domain.FinCrimeCheckCaseStatus;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.PaymentCaseEntityV2;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.PaymentCaseRepositoryV2;
import com.tenx.fraudamlmanager.infrastructure.transactionmanager.TransactionManagerClient;
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.domain.DomesticOutPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.DomesticFinCrimeCheckResultNotificationService;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultResponseCodeV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultServiceImplV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.TransactionManagerConnector;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.infrastructure.DomesticFinCrimeCheckResultProducerV2;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class FinCrimeCheckResultV2IntegrationTest extends KafkaTestBase {

  private static final String FIN_CRIME_CHECK_RESULT = "/v2/payments/finCrimeCheckResult";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objMap;

  @SpyBean
  private FinCrimeCheckResultServiceV2 finCrimeCheckResultServiceV2;

  @SpyBean
  private DomesticFinCrimeCheckResultNotificationService domesticFinCrimeCheckResultNotificationService;

  @SpyBean
  private CaseProcessingService caseProcessingService;

  @SpyBean
  private DomesticFinCrimeCheckResultProducerV2 domesticFinCrimeCheckResultProducerV2;

  @SpyBean
  private TransactionManagerConnector transactionManagerConnector;

  @MockBean
  private PaymentCaseRepositoryV2 paymentCaseRepository;

  @MockBean
  private TransactionManagerClient transactionManagerClient;

  @Captor
  private ArgumentCaptor<FPSFraudCheckResponse> fpsFraudCheckResponseArgumentCaptor;

  private Consumer<String, FPSFraudCheckResponse> fpsFraudCheckResponseConsumer;

  @Value("${spring.kafka.producer.fps-fraud-check-response-event-topic}")
  private String topic;

  @Test
  void checkFinCrimeProcessGivenDomesticFinCrimeCheckResultReceiverIsEmpty()
      throws Exception {
    FinCrimeCheckResultV2 finCrimeCheckResult = new FinCrimeCheckResultV2("transactionId",
        FinCrimeCheckResultResponseCodeV2.PASSED);

    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.PASSED);

    String jsonString = objMap.writeValueAsString(finCrimeCheckResult);
    mockMvc.perform(post(FIN_CRIME_CHECK_RESULT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonString))
        .andExpect(status().is2xxSuccessful());

    verify(caseProcessingService, VerificationModeFactory.times(1))
        .processCaseForFinCrimeCheckResult(eq(finCrimeCheckCase));
    verify(transactionManagerConnector, times(1)).notifyTransactionManager(finCrimeCheckResult);
    verify(domesticFinCrimeCheckResultNotificationService, times(0))
        .notifyDomesticFinCrimeCheckResult(finCrimeCheckResult);
  }

  @Test
  void checkFinCrimeProcessGivenDomesticFinCrimeCheckResultReceiverIsTransactionManager()
      throws Exception {
    ReflectionTestUtils
        .setField(finCrimeCheckResultServiceV2, "finCrimeCheckResultReceiver",
            FinCrimeCheckResultServiceImplV2.FinCrimeCheckResultReceiver.TRANSACTION_MANAGER);

    FinCrimeCheckResultV2 finCrimeCheckResultV2 = new FinCrimeCheckResultV2("transactionId",
        FinCrimeCheckResultResponseCodeV2.PASSED);

    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.PASSED);

    String jsonString = objMap.writeValueAsString(finCrimeCheckResultV2);
    mockMvc.perform(post(FIN_CRIME_CHECK_RESULT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonString))
        .andExpect(status().is2xxSuccessful());

    verify(caseProcessingService, VerificationModeFactory.times(1))
        .processCaseForFinCrimeCheckResult(eq(finCrimeCheckCase));
    verify(transactionManagerConnector, times(1)).notifyTransactionManager(finCrimeCheckResultV2);
    verify(domesticFinCrimeCheckResultNotificationService, times(0))
        .notifyDomesticFinCrimeCheckResult(finCrimeCheckResultV2);
  }

  @Test
  void checkFinCrimeProcessGivenDomesticFinCrimeCheckResultReceiverIsFPSRails()
      throws Exception {
    ReflectionTestUtils.setField(finCrimeCheckResultServiceV2, "finCrimeCheckResultReceiver",
        FinCrimeCheckResultServiceImplV2.FinCrimeCheckResultReceiver.FPSRAILS);

    FinCrimeCheckResultV2 finCrimeCheckResultV2 = new FinCrimeCheckResultV2("transactionId",
        FinCrimeCheckResultResponseCodeV2.PASSED);

    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.PASSED);

    PaymentCaseEntityV2 paymentCaseEntityV2 = new PaymentCaseEntityV2();
    paymentCaseEntityV2.setTransactionId("txnId");
    paymentCaseEntityV2.setPaymentType(DomesticOutPaymentV2.class.getSimpleName());
    given(paymentCaseRepository.findByTransactionId(finCrimeCheckResultV2.getTransactionId()))
        .willReturn(paymentCaseEntityV2);

    String jsonString = objMap.writeValueAsString(finCrimeCheckResultV2);
    mockMvc.perform(post(FIN_CRIME_CHECK_RESULT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonString))
        .andExpect(status().is2xxSuccessful());

    verify(caseProcessingService, VerificationModeFactory.times(1))
        .processCaseForFinCrimeCheckResult(eq(finCrimeCheckCase));
    verify(transactionManagerConnector, times(0)).notifyTransactionManager(finCrimeCheckResultV2);
    verify(domesticFinCrimeCheckResultNotificationService, times(1))
        .notifyDomesticFinCrimeCheckResult(finCrimeCheckResultV2);
    verify(domesticFinCrimeCheckResultProducerV2, times(1))
        .publishDomesticFinCrimeCheckResult(fpsFraudCheckResponseArgumentCaptor.capture());

    FPSFraudCheckResponse fpsFraudCheckResponse = fpsFraudCheckResponseArgumentCaptor.getValue();
    assertThat(fpsFraudCheckResponse.getStatus())
        .isEqualTo(FinCrimeCheckResultResponseCodeV2.PASSED.name());
    assertThat(fpsFraudCheckResponse.getTransactionId())
        .isEqualTo(finCrimeCheckResultV2.getTransactionId());

    ConsumerRecord<String, FPSFraudCheckResponse> consumerRecord = KafkaTestUtils
        .getSingleRecord(fpsFraudCheckResponseConsumer, topic);
    assertThat(consumerRecord).isNotNull();
    FPSFraudCheckResponse receivedFPSFraudCheckResponse = consumerRecord.value();
    assertThat(receivedFPSFraudCheckResponse.getTransactionId())
        .isEqualTo(finCrimeCheckResultV2.getTransactionId());
  }

  @Override
  @BeforeEach
  public void initTest() {
    fpsFraudCheckResponseConsumer = new DefaultKafkaConsumerFactory<String, FPSFraudCheckResponse>(
        consumerProps).createConsumer(CONSUMER_GROUP_ID, CLIENT_PREFIX);

    fpsFraudCheckResponseConsumer
        .subscribe(new ArrayList<>(List.of(topic)));

  }

  @Override
  @AfterEach
  public void resetTest() {
    fpsFraudCheckResponseConsumer.close();
  }
}
