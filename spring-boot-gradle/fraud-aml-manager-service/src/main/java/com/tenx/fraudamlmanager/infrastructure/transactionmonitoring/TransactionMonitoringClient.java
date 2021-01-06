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
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment;
import com.tenx.fraudamlmanager.registration.infrastructure.RegistrationDetails;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Massimo Della Rovere
 */
@FeignClient(name = "transaction-monitoring-adapter", url = "${transactionmonitoring.url}", configuration = TransactionMonitoringConfig.class)
@ConditionalOnProperty(value = "transactionmonitoring.enableMock", havingValue = "false", matchIfMissing = true)
public interface TransactionMonitoringClient {

    /**
     * @param domesticOutPayment the payload to pass
     */
    @PostMapping(value = "/v1/payments/domesticPaymentOutboundFinCrimeCheck", consumes = "application/json")
    FraudAMLSanctionsCheckResponse checkFinCrime(@RequestBody DomesticOutPayment domesticOutPayment)
        throws TransactionMonitoringException;

    /**
     * @param domesticInPayment the payload to pass
     */
    @PostMapping(value = "/v1/payments/domesticPaymentInboundFinCrimeCheck", consumes = "application/json")
    FraudAMLSanctionsCheckResponse checkFinCrime(@RequestBody DomesticInPayment domesticInPayment)
        throws TransactionMonitoringException;

    /**
     * @param onUsPayment the payload to pass
     */
    @PostMapping(value = "/v1/payments/onUsFinCrimeCheck", consumes = "application/json")
    FraudAMLSanctionsCheckResponse checkFinCrime(@RequestBody OnUsPayment onUsPayment)
        throws TransactionMonitoringException;


    /**
     * @param directCreditBacsPayment payload to pass
     */
    @PostMapping(value = "/v1/payments/directCreditFinCrimeCheck", consumes = "application/json")
    FraudAMLSanctionsCheckResponse checkFinCrimeDirectCredit(
        @RequestBody DirectCreditBacsPayment directCreditBacsPayment) throws TransactionMonitoringException;

    /**
     * @param directDebitBacsPayment payload to pass
     */
    @PostMapping(value = "/v1/payments/directDebitFinCrimeCheck", consumes = "application/json")
    FraudAMLSanctionsCheckResponse checkFinCrimeDirectDebit(@RequestBody DirectDebitBacsPayment directDebitBacsPayment)
        throws TransactionMonitoringException;

    /**
     * @param businessPartyDetails the payload to pass
     */
    @PostMapping(value = "/v1/parties/businessParty", consumes = "application/json")
    void sendBusinessPartyEvent(@RequestBody BusinessPartyDetails businessPartyDetails)
        throws TransactionMonitoringException;

    /**
     * @param individualPartyDetails the payload to pass
     */
    @PostMapping(value = "/v1/parties/individualParty/", consumes = "application/json")
    void sendIndividualPartyEvent(@RequestBody IndividualPartyDetails individualPartyDetails)
        throws TransactionMonitoringException;

    /**
     * @param payeeData the payload to pass
     */
    @PostMapping(value = "/v1/parties/payee/", consumes = "application/json")
    void sendPayeeEvent(@RequestBody PayeeData payeeData) throws TransactionMonitoringException;


    /**
     * @param registrationDetails the payload to pass
     */
    @PostMapping(value = "/v1/registration/", consumes = "application/json")
    void sendCustomerRegistrationEvent(@RequestBody RegistrationDetails registrationDetails)
        throws TransactionMonitoringException;

    /**
     * @param loginAttempts the payload to pass
     */
    @PostMapping(value = "/v1/authentication/login", consumes = "application/json")
    void sendLoginAttemptsEvent(@RequestBody LoginAttempts loginAttempts)
        throws TransactionMonitoringException;

    /**
     * @param stepUpPayload the payload to pass
     */
    @PostMapping(value = "/v1/authentication/stepUp", consumes = "application/json")
    void sendStepUpEvent(@RequestBody StepUpPayload stepUpPayload)
        throws TransactionMonitoringException;

    /**
     * @param authReset the payload to pass
     */
    @PostMapping(value = "/v1/authentication/reset", consumes = "application/json")
    void sendIdentityAccountResetEvent(@RequestBody AuthReset authReset)
        throws TransactionMonitoringException;

    /**
     * @param setupMandates the payload to pass
     */
    @PostMapping(value = "/v1/beneficiaries/mandates", consumes = "application/json")
    void sendMandatesEvent(@RequestBody SetupMandates setupMandates)
        throws TransactionMonitoringException;
}