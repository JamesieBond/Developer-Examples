package com.tenx.fraudamlmanager.deviceprofile.domain;

import java.util.HashMap;

public interface PaymentsDeviceProfileService {

  HashMap<String, Object> getThreatmetrixDataForPayment(String partyKey, String deviceKeyId);

  PaymentDeviceProfile fetchThreatMetrixResultUsingPartyKey(String partyKey);

}
