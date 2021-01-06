package com.tenx.fraudamlmanager.authentication.reset.infrastructure;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthReset {

    @NotEmpty
    @NotNull
    private String partyKey;

    @NotNull
    private IdentityAccountReset result;

}
