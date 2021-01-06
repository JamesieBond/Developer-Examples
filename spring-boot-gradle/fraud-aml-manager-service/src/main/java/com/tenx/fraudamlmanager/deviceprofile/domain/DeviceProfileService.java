package com.tenx.fraudamlmanager.deviceprofile.domain;

import com.tenx.fraudamlmanager.deviceprofile.infrastructure.DeviceProfileEntity;

public interface DeviceProfileService {

  DeviceProfileEntity retrieveDeviceProfileWith(String partyKey);

  DeviceProfileEntity retrieveDeviceProfileWith(String partyKey, String deviceKeyId);
}
