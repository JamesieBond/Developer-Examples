package com.tenx.fraudamlmanager.deviceprofile.infrastructure;

import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentDeviceProfile;
import com.tenx.fraudamlmanager.threatmetrix.infrastructure.ThreatMetrixAdapterConfig;
import java.util.HashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Niall O'Connell
 */
@FeignClient(
		name = "threatmetrix-adapter",
		url = "${threatmetrixadapter.url}",
		configuration = ThreatMetrixAdapterConfig.class)
@ConditionalOnProperty(
		value = "threatmetrix.enableMock",
		havingValue = "false",
		matchIfMissing = true)
public interface ThreatMetrixAdapterClient {

  /**
   * @param sessionId payload to pass
   */
  @GetMapping(value = "/v1/session-query/{sessionId}", consumes = "application/json")
  HashMap<String, Object> getThreatmetrixData(@PathVariable("sessionId") String sessionId);


  /**
   * @param partyKey payload to pass
   */
  @GetMapping(value = "/v1/deviceProfilingByPartyKey/{partyKey}", consumes = "application/json")
  PaymentDeviceProfile getThreatmetrixDataByPartyKey(@PathVariable("partyKey") String partyKey);
}
