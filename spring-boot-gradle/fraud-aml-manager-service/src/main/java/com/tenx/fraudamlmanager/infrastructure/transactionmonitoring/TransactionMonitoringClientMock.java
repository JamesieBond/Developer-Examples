package com.tenx.fraudamlmanager.infrastructure.transactionmonitoring;

import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.LoginAttempts;
import com.tenx.fraudamlmanager.authentication.reset.infrastructure.AuthReset;
import com.tenx.fraudamlmanager.authentication.stepup.infrastructure.StepUpPayload;
import com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure.SetupMandates;
import com.tenx.fraudamlmanager.onboarding.business.domain.BusinessPartyDetails;
import com.tenx.fraudamlmanager.onboarding.individual.api.IndividualPartyDetails;
import com.tenx.fraudamlmanager.onboarding.payee.api.PayeeData;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DirectCreditBacsPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DirectDebitBacsPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticInPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticOutPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.FraudAMLSanctionsCheckResponse;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.FraudAMLSanctionsCheckResponse.ResponseCode;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment;
import com.tenx.fraudamlmanager.registration.infrastructure.RegistrationDetails;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** @author James Spencer */
@Service
@Slf4j
@ConditionalOnProperty(value = "transactionmonitoring.enableMock", havingValue = "true")
public class TransactionMonitoringClientMock implements TransactionMonitoringClient {

  /** @param domesticOutPayment the payload to pass */
  @PostMapping(
      value = "/v1/payments/domesticPaymentOutboundFinCrimeCheck",
      consumes = "application/json")
  public FraudAMLSanctionsCheckResponse checkFinCrime(
      @RequestBody DomesticOutPayment domesticOutPayment) {
    return new FraudAMLSanctionsCheckResponse(
        domesticOutPayment.getTransactionId(), ResponseCode.PASSED.toString(), new ArrayList<>());
  }

  /** @param domesticInPayment the payload to pass */
  @PostMapping(
      value = "/v1/payments/domesticPaymentInboundFinCrimeCheck",
      consumes = "application/json")
  public FraudAMLSanctionsCheckResponse checkFinCrime(
      @RequestBody DomesticInPayment domesticInPayment) {
    return new FraudAMLSanctionsCheckResponse(
        domesticInPayment.getTransactionId(), ResponseCode.PASSED.toString(), new ArrayList<>());
  }

  /** @param onUsPayment the payload to pass */
  @PostMapping(value = "/v1/payments/onUsFinCrimeCheck", consumes = "application/json")
  public FraudAMLSanctionsCheckResponse checkFinCrime(@RequestBody OnUsPayment onUsPayment) {
    return new FraudAMLSanctionsCheckResponse(
        onUsPayment.getTransactionId(), ResponseCode.PASSED.toString(), new ArrayList<>());
  }

  /** @param directCreditBacsPayment payload to pass */
  @PostMapping(value = "/v1/payments/directCreditFinCrimeCheck", consumes = "application/json")
  public FraudAMLSanctionsCheckResponse checkFinCrimeDirectCredit(
      @RequestBody DirectCreditBacsPayment directCreditBacsPayment) {
    return new FraudAMLSanctionsCheckResponse(
        directCreditBacsPayment.getTransactionId(),
        ResponseCode.PASSED.toString(),
        new ArrayList<>());
  }

  /** @param directDebitBacsPayment payload to pass */
  @PostMapping(value = "/v1/payments/directDebitFinCrimeCheck", consumes = "application/json")
  public FraudAMLSanctionsCheckResponse checkFinCrimeDirectDebit(
      @RequestBody DirectDebitBacsPayment directDebitBacsPayment) {
    return new FraudAMLSanctionsCheckResponse(
        directDebitBacsPayment.getTransactionId(),
        ResponseCode.PASSED.toString(),
        new ArrayList<>());
  }

  /** @param businessPartyDetails the payload to pass */
  @PostMapping(value = "/v1/parties/businessParty", consumes = "application/json")
  public void sendBusinessPartyEvent(@RequestBody BusinessPartyDetails businessPartyDetails) {
    log.info("Mock TransactionMonitoring has been called for V1 BusinessPartyEvent");
  }

  /** @param individualPartyDetails the payload to pass */
  @PostMapping(value = "/v1/parties/individualParty/", consumes = "application/json")
  public void sendIndividualPartyEvent(@RequestBody IndividualPartyDetails individualPartyDetails) {
    log.info("Mock TransactionMonitoring has been called for V1 IndividualPartyEvent");
  }

  /** @param payeeData the payload to pass */
  @PostMapping(value = "/v2/parties/newPayee", consumes = "application/json")
  public void sendPayeeEvent(@RequestBody PayeeData payeeData) {
    log.info("Mock TransactionMonitoring has been called for V2 PayeeEvent");
  }

  /** @param registrationDetails the payload to pass */
  @PostMapping(value = "/v1/registration/", consumes = "application/json")
  public void sendCustomerRegistrationEvent(@RequestBody RegistrationDetails registrationDetails) {
    log.info("Mock TransactionMonitoring has been called for V1 CustomerRegistrationEvent");
  }

  /** @param loginAttempts the payload to pass */
  @PostMapping(value = "/v1/authentication/login", consumes = "application/json")
  public void sendLoginAttemptsEvent(@RequestBody LoginAttempts loginAttempts) {
    log.info("Mock TransactionMonitoring has been called for V1 loginAttempts");
  }

  /** @param stepUpPayload the payload to pass */
  @PostMapping(value = "/v1/authentication/stepUp", consumes = "application/json")
  public void sendStepUpEvent(@RequestBody StepUpPayload stepUpPayload) {
    log.info("Mock TransactionMonitoring has been called for V1 stepUp");
  }

  /** @param authReset the payload to pass */
  @PostMapping(value = "/v1/authentication/reset", consumes = "application/json")
  public void sendIdentityAccountResetEvent(@RequestBody AuthReset authReset)
      throws TransactionMonitoringException {
    log.info("Mock TransactionMonitoring has been called for V1 reset");
  }

  /** @param setupMandates the payload to pass */
  @PostMapping(value = "/v1/beneficiaries/mandates", consumes = "application/json")
  public void sendMandatesEvent(@RequestBody SetupMandates setupMandates)
      throws TransactionMonitoringException {
    log.info("Mock TransactionMonitoring has been called for V1 mandates");
  }
}

