package com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.SpringBootTestBase;
import com.tenx.fraudamlmanager.paymentsv3.domain.ExternalCaseDetailsV3;
import com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain.FinCrimeCheckResultResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain.FinCrimeCheckResultServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain.FinCrimeCheckResultV3;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class FinCrimeCheckResultV3IntegrationTest extends SpringBootTestBase {

  private static final String FIN_CRIME_CHECK_RESULT = "/v3/payments/finCrimeCheckResult";

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objMap;

  @SpyBean
  FinCrimeCheckResultServiceV3 finCrimeCheckResultService;

  @Captor
  ArgumentCaptor<FinCrimeCheckResultV3> fraudPaymentCheckArgumentCaptor;

  @Test
  void finCrimeCheckResult2xx() throws Exception {
    List<ExternalCaseDetailsV3> externalCaseDetailsList = new ArrayList<>();
    FinCrimeCheckResultV3 finCrimeCheckResult = new FinCrimeCheckResultV3("transactionId",
        FinCrimeCheckResultResponseCodeV3.PASSED, externalCaseDetailsList);

    String jsonString = objMap.writeValueAsString(finCrimeCheckResult);
    mockMvc.perform(post(FIN_CRIME_CHECK_RESULT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonString))
        .andExpect(status().is2xxSuccessful());

    verify(finCrimeCheckResultService, times(1))
        .updateFinCrimeCheck(fraudPaymentCheckArgumentCaptor.capture());
    FinCrimeCheckResultV3 finCrimeCheckResultV3 = fraudPaymentCheckArgumentCaptor.getValue();
    assertThat(finCrimeCheckResultV3.getStatus()).isEqualTo(FinCrimeCheckResultResponseCodeV3.PASSED);
    assertThat(finCrimeCheckResultV3.getTransactionId())
        .isEqualTo(finCrimeCheckResult.getTransactionId());
  }

  @Test
  void finCrimeCheckResult4xxError() throws Exception {
    FinCrimeCheckResultV3 finCrimeCheckResult = new FinCrimeCheckResultV3();

    mockMvc.perform(post(FIN_CRIME_CHECK_RESULT)
        .content(objMap.writeValueAsString(finCrimeCheckResult))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }
}
