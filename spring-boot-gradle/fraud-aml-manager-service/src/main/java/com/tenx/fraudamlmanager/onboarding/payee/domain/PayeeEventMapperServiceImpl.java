package com.tenx.fraudamlmanager.onboarding.payee.domain;

import com.tenx.fraudamlmanager.onboarding.payee.api.ChangeType;
import com.tenx.fraudamlmanager.onboarding.payee.api.PayeeBeneficiary;
import com.tenx.fraudamlmanager.onboarding.payee.api.PayeeData;
import com.tenx.payeemanager.event.payee.PayeesCreate;
import com.tenx.payeemanager.event.payee.PayeesDelete;
import com.tenx.payeemanager.event.payee.PayeesUpdate;
import org.springframework.stereotype.Service;

// This class cannot be removed because the extract methods should be defined in the source object
// of MapStruct. But that class is an autogenerated from AVRO, so we cannot add code there.
// The extract method were added to the destination object.
// For this reason we cannot delegate to the mapper the job.
// We cannot extract from a property of destination object to set in another property of the same object.
// We cannot be sure of the priority in calling the set methods from MapStruct, so the extract could occur on a property not yet valorised.

@Service
public class PayeeEventMapperServiceImpl implements PayeeEventMapperService {

    public PayeeData mapPayeeCreateDetails(PayeesCreate payeesCreate) {
        PayeeData payeeData = PayeeCreateEventMapper.MAPPER.toPayee(payeesCreate);
        payeeData.setBeneficiary(new PayeeBeneficiary(
                payeeData.extractBeneficiaryId().orElse(""),
                payeeData.extractBeneficiaryFirstName(),
                payeeData.extractBeneficiaryLastName(),
                payeesCreate.getReference()));
        payeeData.setChangeType(ChangeType.CREATE);
        return payeeData;
    }

    public PayeeData mapPayeeUpdateDetails(PayeesUpdate payeesUpdate) {
        PayeeData payeeUpdateData = PayeesUpdateEventMapper.MAPPER.toPayeeUpdate(payeesUpdate);
        PayeeBeneficiary payeeBeneficiary = new PayeeBeneficiary();
        payeeBeneficiary.setFirstName(payeeUpdateData.extractBeneficiaryFirstNameForPayeesUpdate());
        payeeBeneficiary.setLastName(payeeUpdateData.extractBeneficiaryLastNameForPayeesUpdate());
        payeeBeneficiary.setId(payeeUpdateData.getAccountId());
        payeeUpdateData.setBeneficiary(payeeBeneficiary);
        payeeUpdateData.setChangeType(ChangeType.UPDATE);
        return payeeUpdateData;
    }

    public PayeeData mapPayeeDeleteDetails(PayeesDelete payeesDelete) {
        PayeeData payeeUpdateData = PayeesDeleteEventMapper.MAPPER.toPayeeDelete(payeesDelete);
        payeeUpdateData.setBeneficiary(new PayeeBeneficiary(
                payeeUpdateData.extractBeneficiaryId().orElse(""),
                payeeUpdateData.extractBeneficiaryFirstName(),
                payeeUpdateData.extractBeneficiaryLastName()    ,
                payeesDelete.getReference()));
        payeeUpdateData.setChangeType(ChangeType.DELETE);
        return payeeUpdateData;
    }

}
