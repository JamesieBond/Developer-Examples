package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultResponseCodeV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultServiceImplV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultV2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FinCrimeCheckResultControllerV2.class)
public class FinCrimeCheckResultControllerTestV2 {

  private static final String FIN_CRIME_CHECK_RESULT = "/v2/payments/finCrimeCheckResult";

  @Autowired
  MockMvc mockMvc;

  @MockBean
  FinCrimeCheckResultServiceImplV2 finCrimeCheckService;

  @Autowired
  ObjectMapper objMap;

  @Test
  public void finCrimeCheckResult2xx() throws Exception {
    FinCrimeCheckResultV2 finCrimeCheckResult = new FinCrimeCheckResultV2("transactionId",
      FinCrimeCheckResultResponseCodeV2.PASSED);

    String jsonString = objMap.writeValueAsString(finCrimeCheckResult);
    doNothing().when(finCrimeCheckService).updateFinCrimeCheck(any(FinCrimeCheckResultV2.class));
    mockMvc.perform(post(FIN_CRIME_CHECK_RESULT)
      .contentType(MediaType.APPLICATION_JSON_UTF8)
      .content(jsonString))
      .andExpect(status().is2xxSuccessful());
  }

  @Test
  public void finCrimeCheckResultt4xxError() throws Exception {
    FinCrimeCheckResultV2 finCrimeCheckResult = new FinCrimeCheckResultV2();

    mockMvc.perform(post(FIN_CRIME_CHECK_RESULT)
      .content(objMap.writeValueAsString(finCrimeCheckResult))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().is4xxClientError());
  }

}
