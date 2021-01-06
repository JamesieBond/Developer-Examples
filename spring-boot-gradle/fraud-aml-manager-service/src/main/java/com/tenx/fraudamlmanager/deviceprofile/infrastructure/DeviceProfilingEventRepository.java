package com.tenx.fraudamlmanager.deviceprofile.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceProfilingEventRepository extends JpaRepository<DeviceProfileEntity, Long> {

  DeviceProfileEntity findByPartyKeyAndDeviceKeyId(String partyKey, String deviceKeyId);

  DeviceProfileEntity findFirstByPartyKeyOrderByUpdatedDateDesc(String partyKey);
}
