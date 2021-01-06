package com.tenx.fraudamlmanager.deviceprofile.infrastructure;

import com.tenx.fraudamlmanager.deviceprofile.domain.DeviceProfilingService;
import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentDeviceProfile;
import com.tenx.fraudamlmanager.threatmetrix.infrastructure.deviceprofile.PaymentDeviceProfilingException;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceProfileFetchDeviceKey {

  private final DeviceProfilingService deviceProfilingService;

  private static final String DEVICE_PROFILING_WARN = "Could not retrieve device profile data for partyKey : {} from TMXA";

  public PaymentDeviceProfile fetchTMXADataUsingPartyKey(PaymentDeviceProfile deviceProfile, String partyKey) {
    PaymentDeviceProfile paymentDeviceProfile = null;
    try {
      paymentDeviceProfile = (deviceProfile == null || deviceProfile.isDeviceKeyIdOrPartyKeyNotPresent()) ?
          deviceProfilingService.retrieveAndSaveDeviceProfilingEventDataUsingPartyKey(partyKey)
          : deviceProfile;
    } catch (PaymentDeviceProfilingException e) {
      log.warn(DEVICE_PROFILING_WARN, partyKey);
    }
    if (paymentDeviceProfile == null) {
      paymentDeviceProfile = new PaymentDeviceProfile();
    }
    if (paymentDeviceProfile.isDeviceKeyIdOrPartyKeyNotPresent()) {
      paymentDeviceProfile.setDeviceKeyId("unknown");
      paymentDeviceProfile.setDeviceProfile(new HashMap<>());
    }
    return paymentDeviceProfile;
  }

}