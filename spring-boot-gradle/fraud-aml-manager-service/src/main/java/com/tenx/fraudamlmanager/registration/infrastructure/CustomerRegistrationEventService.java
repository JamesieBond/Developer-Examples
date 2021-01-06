package com.tenx.fraudamlmanager.registration.infrastructure;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.security.forgerockfacade.resource.CustomerRegistration;

public interface CustomerRegistrationEventService {

    void processCustomerRegistrationEvent(CustomerRegistration customerRegistration) throws TransactionMonitoringException;

}
