package com.tenx.fraudamlmanager.deviceprofile.infrastructure;

import com.tenx.fraudamlmanager.deviceprofile.domain.DeviceProfileEntityToPaymentDeviceProfileMapper;
import com.tenx.fraudamlmanager.deviceprofile.domain.DeviceProfileExtractor;
import com.tenx.fraudamlmanager.deviceprofile.domain.DeviceProfileService;
import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentDeviceProfile;
import com.tenx.fraudamlmanager.paymentsv2.domain.PaymentDeviceProfileServiceV2;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentDeviceProfileServiceImplV2 implements PaymentDeviceProfileServiceV2 {

  private final DeviceProfileService deviceProfileService;
  private final DeviceProfileExtractor deviceProfileExtractor;
  private final DeviceProfileFetchDeviceKey deviceProfileFetchDeviceKey;

  @Override
  public HashMap<String, Object> getDeviceProfileForPayment(String partyKey, String deviceKeyId) {
    log.info("Find device profile data for payment. PartyKey: {}", partyKey);
    PaymentDeviceProfile paymentDeviceProfile =
            (deviceKeyId == null || deviceKeyId.isEmpty()) ? fetchThreatMetrixResultUsingPartyKey(partyKey)
                    : fetchThreatMetrixResultUsingPartyKeyAndDeviceKeyId(partyKey, deviceKeyId);
    log.info("Extracting deviceProfile data for deviceKeyId: {}", paymentDeviceProfile.getDeviceKeyId());
    return deviceProfileExtractor
            .extractSpecificDeviceProfileData(paymentDeviceProfile.getDeviceProfile(),
                    paymentDeviceProfile.getDeviceKeyId());
  }

  private DeviceProfileEntity getDeviceProfileEntity(String partyKey) {
    return deviceProfileService.retrieveDeviceProfileWith(partyKey);
  }

  private DeviceProfileEntity getDeviceProfileEntity(String partyKey, String deviceKeyId) {
    return getDeviceProfileEntityData(
        deviceProfileService.retrieveDeviceProfileWith(partyKey, deviceKeyId));
  }

  private DeviceProfileEntity getDeviceProfileEntityData(DeviceProfileEntity deviceProfileEntity) {
    // TODO: 2020-06-26 Move logic to DeviceProfileEntity 
    if (deviceProfileEntity == null) {
      deviceProfileEntity = new DeviceProfileEntity();
      deviceProfileEntity.setDeviceKeyId("unknown");
      deviceProfileEntity.setDeviceProfile(new HashMap<>());
    }
    return deviceProfileEntity;
  }

  private PaymentDeviceProfile fetchThreatMetrixResultUsingPartyKey(String partyKey) {
    return deviceProfileFetchDeviceKey.fetchTMXADataUsingPartyKey(
        DeviceProfileEntityToPaymentDeviceProfileMapper.MAPPER.toPaymentDeviceProfile(getDeviceProfileEntity(partyKey)),
        partyKey);

  }

  private PaymentDeviceProfile fetchThreatMetrixResultUsingPartyKeyAndDeviceKeyId(String partyKey, String deviceKeyId) {
    return DeviceProfileEntityToPaymentDeviceProfileMapper.MAPPER
        .toPaymentDeviceProfile(getDeviceProfileEntity(partyKey, deviceKeyId));

  }
}

