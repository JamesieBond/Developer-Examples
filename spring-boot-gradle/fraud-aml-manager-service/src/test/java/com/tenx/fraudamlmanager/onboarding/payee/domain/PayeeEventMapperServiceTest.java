package com.tenx.fraudamlmanager.onboarding.payee.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tenx.fraudamlmanager.onboarding.payee.api.ChangeType;
import com.tenx.fraudamlmanager.onboarding.payee.api.PayeeData;
import com.tenx.payeemanager.event.payee.PayeeAccount;
import com.tenx.payeemanager.event.payee.PayeesCreate;
import com.tenx.payeemanager.event.payee.PayeesDelete;
import com.tenx.payeemanager.event.payee.PayeesUpdate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PayeeEventMapperServiceTest {

    private PayeeEventMapperServiceImpl payeeCreateEventMapperService;

    @BeforeEach
    public void beforeEach() {
        this.payeeCreateEventMapperService = new PayeeEventMapperServiceImpl();
    }

    @Test
    public void checkPayeeCreateEventMapperService() {
        List<PayeeAccount> payeeAccountList = new ArrayList<PayeeAccount>();
        payeeAccountList.add(
                com.tenx.payeemanager.event.payee.PayeeAccount.newBuilder()
                        .setName("name")
                        .setId("payeeAccountId")
                        .setIdentificationType("identificationType")
                        .setIdentification("identification")
                        .build());

        PayeesCreate payeesCreateEvent =
                PayeesCreate.newBuilder()
                        .setPartyKey("partyKey")
                        .setPayeeId("payeeId")
                        .setPayeeName("payeeName")
                        .setCategory("category")
                        .setReference("reference")
                        .setUpdatedDateTime("2019-08-08")
                        .setCreatedDateTime("2019-08-08")
                        .setPayeeAccounts(payeeAccountList)
                        .setPayerPartyKey("payerPartyKey")
                        .setPayerPartyType("type")
                        .setPayerPartyStatus("status")
                        .build();

        PayeeData payeeData = payeeCreateEventMapperService
                .mapPayeeCreateDetails(payeesCreateEvent);
        assertEquals(payeeData.getAccountId(), payeesCreateEvent.getPayerPartyKey());
        assertEquals(payeeData.getPartyKey(), payeesCreateEvent.getPartyKey());
        assertEquals(payeeData.getPayeeId(), payeesCreateEvent.getPayeeId());
        assertEquals(payeeData.getAuthenticationMethod(), "none");
        assertEquals(payeeData.getBeneficiary().getId(), payeesCreateEvent.getPayeeAccounts().get(0).getIdentification());
        assertEquals(payeeData.getBeneficiary().getReference(), payeesCreateEvent.getReference());
        assertEquals(payeeData.getBeneficiary().getFirstName(), payeeData.extractBeneficiaryFirstName());
        assertEquals(payeeData.getChangeType(), ChangeType.CREATE);
    }

    @Test
    public void checkPayeeDeleteEventMapperService() {
        List<PayeeAccount> payeeAccountList = new ArrayList<PayeeAccount>();
        payeeAccountList.add(
                com.tenx.payeemanager.event.payee.PayeeAccount.newBuilder()
                        .setName("name")
                        .setId("payeeAccountId")
                        .setIdentificationType("identificationType")
                        .setIdentification("identification")
                        .build());

        PayeesDelete payeesDeleteData =
                PayeesDelete.newBuilder()
                        .setPartyKey("partyKey")
                        .setPayeeId("payeeId")
                        .setPayeeName("payeeName")
                        .setCategory("category")
                        .setReference("reference")
                        .setUpdatedDateTime("2019-08-08")
                        .setCreatedDateTime("2019-08-08")
                        .setPayeeAccounts(payeeAccountList)
                        .setPayerPartyKey("payerPartyKey")
                        .setPayerPartyType("type")
                        .setPayerPartyStatus("status")
                        .build();

        PayeeData payeeData = payeeCreateEventMapperService
                .mapPayeeDeleteDetails(payeesDeleteData);
        assertEquals(payeeData.getAccountId(), payeesDeleteData.getPayerPartyKey());
        assertEquals(payeeData.getPartyKey(), payeesDeleteData.getPartyKey());
        assertEquals(payeeData.getPayeeId(), payeesDeleteData.getPayeeId());
        assertEquals(payeeData.getAuthenticationMethod(), "none");
        assertEquals(payeeData.getBeneficiary().getId(), payeesDeleteData.getPayeeAccounts().get(0).getIdentification());
        assertEquals(payeeData.getBeneficiary().getReference(), payeesDeleteData.getReference());
        assertEquals(payeeData.getBeneficiary().getFirstName(), payeeData.extractBeneficiaryFirstName());
        assertEquals(payeeData.getChangeType(), ChangeType.DELETE);
    }

    @Test
    public void checkPayeeUpdateEventMapperService() {

        PayeesUpdate payeesUpdateData =
                PayeesUpdate.newBuilder()
                        .setHeaderPartyKey("partyKey")
                        .setPayeeId("payeeId")
                        .setName("payeeName")
                        .setAccountId("category")
                        .build();

        PayeeData payeeData = payeeCreateEventMapperService
                .mapPayeeUpdateDetails(payeesUpdateData);
        assertEquals(payeeData.getAccountId(), payeesUpdateData.getAccountId());
        assertEquals(payeeData.getPartyKey(), payeesUpdateData.getHeaderPartyKey());
        assertEquals(payeeData.getPayeeId(), payeesUpdateData.getPayeeId());
        assertEquals(payeeData.getName(), payeesUpdateData.getName());
        assertEquals(payeeData.getAuthenticationMethod(), "none");
        assertEquals(payeeData.getBeneficiary().getId(), payeesUpdateData.getAccountId());
        assertEquals(payeeData.getBeneficiary().getFirstName(), payeeData.extractBeneficiaryFirstNameForPayeesUpdate());
        assertEquals(payeeData.getChangeType(), ChangeType.UPDATE);
    }


}
