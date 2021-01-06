package com.tenx.fraudamlmanager.onboarding.payee.domain;

import com.tenx.fraudamlmanager.onboarding.payee.api.PayeeData;
import com.tenx.payeemanager.event.payee.PayeesCreate;
import com.tenx.payeemanager.event.payee.PayeesDelete;
import com.tenx.payeemanager.event.payee.PayeesUpdate;

public interface PayeeEventMapperService {

    PayeeData mapPayeeCreateDetails(PayeesCreate payeesCreate);

    PayeeData mapPayeeUpdateDetails(PayeesUpdate payeesUpdate);

    PayeeData mapPayeeDeleteDetails(PayeesDelete payeesDelete);
}
