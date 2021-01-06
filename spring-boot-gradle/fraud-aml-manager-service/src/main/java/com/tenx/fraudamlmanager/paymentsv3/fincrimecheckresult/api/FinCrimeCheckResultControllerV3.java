package com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.api;

import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;
import com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain.FinCrimeCheckResultServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain.FinCrimeCheckResultV3;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v3/payments")
@Slf4j
@Api(tags = "payments")
public class FinCrimeCheckResultControllerV3 {

    @Autowired
    private FinCrimeCheckResultServiceV3 finCrimeCheckResultService;

    @ApiOperation(value = "Financial Crime check all payments")
    @PostMapping("/finCrimeCheckResult")
    public void processFinCrimeCheckResult(@RequestBody @Valid FinCrimeCheckResultRequestV3 finCrimeCheckResultRequestV3) throws FinCrimeCheckResultException {
        log.info("finCrimeCheckResult request received {}", finCrimeCheckResultRequestV3.getTransactionId());
        FinCrimeCheckResultV3 finCrimeCheckResultV3 = ApiDomainFinCrimeCheckResultMapper.MAPPER.toFinCrimeCheckResultDomainV3(finCrimeCheckResultRequestV3);
        finCrimeCheckResultService.updateFinCrimeCheck(finCrimeCheckResultV3);
    }

}
