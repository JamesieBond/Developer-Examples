package com.tenx.fraudamlmanager.deviceprofile.api;

import com.tenx.fraud.DeviceProfilingEvent;
import com.tenx.fraudamlmanager.deviceprofile.infrastructure.DeviceProfileEntity;
import java.util.HashMap;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeviceProfilingEventEntityMapper {

  DeviceProfilingEventEntityMapper MAPPER =
      Mappers.getMapper(DeviceProfilingEventEntityMapper.class);

  @Mapping(target = "deviceProfile", source = "threatMetrixDeviceProfileResponse")
  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  DeviceProfileEntity toDeviceProfilingEntity(
      DeviceProfilingEvent deviceProfilingEvent,
      HashMap<String, Object> threatMetrixDeviceProfileResponse);
}
