package com.tenx.fraudamlmanager.deviceprofile.infrastructure;

import com.tenx.fraudamlmanager.deviceprofile.domain.DeviceProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceProfileServiceImpl implements DeviceProfileService {

  private final DeviceProfilingEventRepository deviceProfilingEventRepository;

  public DeviceProfileEntity retrieveDeviceProfileWith(String partyKey) {
    return deviceProfilingEventRepository.findFirstByPartyKeyOrderByUpdatedDateDesc(partyKey);
  }

  public DeviceProfileEntity retrieveDeviceProfileWith(String partyKey, String deviceKeyId) {
    return deviceProfilingEventRepository.findByPartyKeyAndDeviceKeyId(partyKey, deviceKeyId);
  }

}
