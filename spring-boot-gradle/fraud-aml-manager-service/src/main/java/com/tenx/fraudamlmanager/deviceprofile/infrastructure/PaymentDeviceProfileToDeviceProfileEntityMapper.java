package com.tenx.fraudamlmanager.deviceprofile.infrastructure;

import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentDeviceProfile;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentDeviceProfileToDeviceProfileEntityMapper {

  PaymentDeviceProfileToDeviceProfileEntityMapper MAPPER =
		  Mappers.getMapper(PaymentDeviceProfileToDeviceProfileEntityMapper.class);

  DeviceProfileEntity toDeviceProfileEntity(PaymentDeviceProfile paymentDeviceProfile);
}
