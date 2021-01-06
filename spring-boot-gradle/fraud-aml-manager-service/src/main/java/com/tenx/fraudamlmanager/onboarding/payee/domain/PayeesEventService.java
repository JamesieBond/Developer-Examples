package com.tenx.fraudamlmanager.onboarding.payee.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.payeemanager.event.payee.PayeesCreate;
import com.tenx.payeemanager.event.payee.PayeesDelete;
import com.tenx.payeemanager.event.payee.PayeesUpdate;

public interface PayeesEventService {

    void processPayeeCreateEvent(PayeesCreate payeesCreate) throws TransactionMonitoringException;

    void processPayeeUpdateEvent(PayeesUpdate payeesUpdate) throws TransactionMonitoringException;

    void processPayeeDeleteEvent(PayeesDelete payeesDelete) throws TransactionMonitoringException;
}
