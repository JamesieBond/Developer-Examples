package com.tenx.fraudamlmanager.paymentsv2.domain;

import java.util.HashMap;

public interface PaymentDeviceProfileServiceV2 {

  HashMap<String, Object> getDeviceProfileForPayment(String partyKey, String deviceKeyId);

}
