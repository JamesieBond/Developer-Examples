package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.api;

import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultV2;
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
@RequestMapping("/v2/payments")
@Slf4j
@Api(tags = "payments")
public class FinCrimeCheckResultControllerV2 {

  @Autowired
  private FinCrimeCheckResultServiceV2 finCrimeCheckResultService;

  @ApiOperation(value = "Financial Crime check all payments")
  @PostMapping("/finCrimeCheckResult")
  public void processFinCrimeCheckResult(
      @RequestBody @Valid FinCrimeCheckResultRequestV2 finCrimeCheckResultRequestV2)
      throws FinCrimeCheckResultException {
    log.info("finCrimeCheckResult request received {}", finCrimeCheckResultRequestV2.getTransactionId());
    FinCrimeCheckResultV2 finCrimeCheckResultV2 = FinCrimeCheckResultMapperV2.MAPPER
        .toFinCrimeCheckResultDomainV2(finCrimeCheckResultRequestV2);
    finCrimeCheckResultService.updateFinCrimeCheck(finCrimeCheckResultV2);
  }

}
