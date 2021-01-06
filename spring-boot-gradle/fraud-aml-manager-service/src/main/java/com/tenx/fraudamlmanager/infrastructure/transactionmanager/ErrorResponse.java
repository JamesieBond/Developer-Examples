package com.tenx.fraudamlmanager.infrastructure.transactionmanager;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {

    private List<Error> errors;
}
