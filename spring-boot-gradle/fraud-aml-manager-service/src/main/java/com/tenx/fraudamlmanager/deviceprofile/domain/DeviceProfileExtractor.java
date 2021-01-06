package com.tenx.fraudamlmanager.deviceprofile.domain;

import java.util.HashMap;

public interface DeviceProfileExtractor {

  HashMap<String, Object> extractSpecificDeviceProfileData(HashMap<String, Object> deviceProfileData,
      String deviceKeyId);
}
