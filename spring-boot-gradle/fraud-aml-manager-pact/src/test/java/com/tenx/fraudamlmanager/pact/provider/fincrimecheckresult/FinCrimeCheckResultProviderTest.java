package com.tenx.fraudamlmanager.pact.provider.fincrimecheckresult;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import au.com.dius.pact.provider.junit.Consumer;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import com.tenx.fraudamlmanager.SpringBootTestBase;
import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultServiceImplV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.infrastructure.DomesticFinCrimeCheckResultProducerV2;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

@Provider("fraudamlmanager.fraud")
@Consumer("transactionmonitoringadapter.fraud")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PactBroker(host = "pactbroker-pact.svc.tooling.mylti3gh7p4x.net/", port = "80")
public class FinCrimeCheckResultProviderTest extends SpringBootTestBase {

  private static final String STATE_SUCCESSFUL_200 = "Fin crime check result endpoint is up";
  private static final String STATE_BAD_REQUEST_400 = "Fin crime check result request is a bad request";
  private static final String STATE_INTERNAL_SERVER_ERROR_500 = "Error occurs in fin crime check result endpoint";

  @MockBean
  private DomesticFinCrimeCheckResultProducerV2 domesticFinCrimeCheckResultProducerV2;

  @LocalServerPort
  int randomServerPort;

  @MockBean
  FinCrimeCheckResultServiceImplV2 finCrimeCheckResultService;

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void pactVerificationTestTemplate(PactVerificationContext context) {
    context.verifyInteraction();
  }

  @BeforeEach
  void TestTarget(PactVerificationContext context) {
    context.setTarget(new HttpTestTarget("localhost", randomServerPort, "/"));
    doNothing().when(domesticFinCrimeCheckResultProducerV2).publishDomesticFinCrimeCheckResult(any());
  }

  @State(STATE_SUCCESSFUL_200)
  public void to200state() throws FinCrimeCheckResultException {
    FinCrimeCheckResultV2 finCrimeCheckResult = new FinCrimeCheckResultV2();
    doNothing().when(finCrimeCheckResultService).updateFinCrimeCheck(finCrimeCheckResult);

  }

  @SneakyThrows
  @State(STATE_BAD_REQUEST_400)
  public void to400state() throws FinCrimeCheckResultException {

    FinCrimeCheckResultV2 finCrimeCheckResult = new FinCrimeCheckResultV2();
    doNothing().when(finCrimeCheckResultService).updateFinCrimeCheck(finCrimeCheckResult);

    doThrow(FinCrimeCheckResultException.class).when(finCrimeCheckResultService).updateFinCrimeCheck(any());
  }

  @SneakyThrows
  @State(STATE_INTERNAL_SERVER_ERROR_500)
  public void to500state() throws FinCrimeCheckResultException {

    doThrow(RuntimeException.class).when(finCrimeCheckResultService).updateFinCrimeCheck(any());
  }
}

