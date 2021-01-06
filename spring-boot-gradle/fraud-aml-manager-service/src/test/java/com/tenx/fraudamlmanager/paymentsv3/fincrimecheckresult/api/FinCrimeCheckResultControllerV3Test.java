package com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.paymentsv3.domain.ExternalCaseDetailsV3;
import com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain.FinCrimeCheckResultResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain.FinCrimeCheckResultServiceV3Impl;
import com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain.FinCrimeCheckResultV3;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FinCrimeCheckResultControllerV3.class)
public class FinCrimeCheckResultControllerV3Test {

    private static final String FIN_CRIME_CHECK_RESULT = "/v3/payments/finCrimeCheckResult";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    FinCrimeCheckResultServiceV3Impl finCrimeCheckService;

    @Autowired
    ObjectMapper objMap;

    @Test
    public void finCrimeCheckResult2xx() throws Exception {
        List<ExternalCaseDetailsV3> externalCaseDetailsList = new ArrayList<>();
        FinCrimeCheckResultV3 finCrimeCheckResult = new FinCrimeCheckResultV3("transactionId", FinCrimeCheckResultResponseCodeV3.PASSED, externalCaseDetailsList);

        String jsonString = objMap.writeValueAsString(finCrimeCheckResult);
        doNothing().when(finCrimeCheckService).updateFinCrimeCheck(any(FinCrimeCheckResultV3.class));
        mockMvc.perform(post(FIN_CRIME_CHECK_RESULT)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonString))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void finCrimeCheckResultt4xxError() throws Exception {
        FinCrimeCheckResultV3 finCrimeCheckResult = new FinCrimeCheckResultV3();

        mockMvc.perform(post(FIN_CRIME_CHECK_RESULT)
                .content(objMap.writeValueAsString(finCrimeCheckResult))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

}
