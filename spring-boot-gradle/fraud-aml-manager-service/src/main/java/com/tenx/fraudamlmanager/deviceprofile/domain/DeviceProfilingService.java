package com.tenx.fraudamlmanager.deviceprofile.domain;

import com.tenx.fraud.DeviceProfilingEvent;
import com.tenx.fraudamlmanager.threatmetrix.infrastructure.deviceprofile.PaymentDeviceProfilingException;
import java.util.HashMap;

public interface DeviceProfilingService {

  void retrieveAndSaveDeviceProfilingEventData(DeviceProfilingEvent deviceProfilingEvent);

  HashMap<String, Object> getThreatmetrixData(String sessionId);

  PaymentDeviceProfile retrieveAndSaveDeviceProfilingEventDataUsingPartyKey(String partyKey)
      throws PaymentDeviceProfilingException;

  PaymentDeviceProfile getThreatmetrixDataByPartyKey(String partyKey) throws PaymentDeviceProfilingException;

}
