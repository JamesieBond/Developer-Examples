package com.tenx.fraudamlmanager.deviceprofile.domain;

import com.tenx.fraudamlmanager.deviceprofile.infrastructure.DeviceProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeviceProfileEntityToPaymentDeviceProfileMapper {

  DeviceProfileEntityToPaymentDeviceProfileMapper MAPPER = Mappers.getMapper(
      DeviceProfileEntityToPaymentDeviceProfileMapper.class);

  PaymentDeviceProfile toPaymentDeviceProfile(DeviceProfileEntity deviceProfileEntity);
}
