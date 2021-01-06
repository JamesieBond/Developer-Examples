package com.tenx.fraudamlmanager.onboarding.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tenxbanking.party.event.business.PartyStatus;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Slf4j
public class BusinessPartyDetails {

    private static final String EVENT_TYPE_CUSTOMER_MODIFIED = "CustomerModified";
    private static final String EVENT_TYPE_CUSTOMER_PROVISIONED = "CustomerProvisioned";

    @NotEmpty
    @NotNull
    private String partyKey;

    @NotEmpty
    @NotNull
    private String updateType;

    @JsonIgnore
    private List<Address> addresses;

    private Address registeredAddress;

    private String companyName;

    private String tradingName;

    private String registrationNumber;

    private String status;

    public void setAddress(List<Address> addresses) {
        this.addresses = addresses;
        Optional<Address> regAddress = extractRegisteredAddress(addresses);
        if (regAddress.isPresent()) {
            this.registeredAddress = regAddress.get();
        } else {
            this.registeredAddress = null;
        }
    }

    private Optional<Address> extractRegisteredAddress(List<Address> addresses) {
        if (CollectionUtils.isEmpty(addresses)) {
            return Optional.empty();
        } else {
            return addresses.stream()
                    .filter(address -> address.isActive() && address.isAddressTypeREGD())
                    .findFirst();
        }
    }

    public boolean isEventTypeCustomerModified() {
        return EVENT_TYPE_CUSTOMER_MODIFIED.equals(updateType);
    }

    public boolean isEventTypeCustomerProvisioned() {
        return EVENT_TYPE_CUSTOMER_PROVISIONED.equals(updateType);
    }

    public boolean isPartyStatusProvisioned() {
        return PartyStatus.PROVISIONED.equals(PartyStatus.valueOf(status));
    }
}
