package com.tenx.fraudamlmanager.deviceprofile.infrastructure;

import com.tenx.fraud.DeviceProfilingEvent;
import com.tenx.fraudamlmanager.deviceprofile.api.DeviceProfilingEventEntityMapper;
import com.tenx.fraudamlmanager.deviceprofile.domain.DeviceProfileMetrics;
import com.tenx.fraudamlmanager.deviceprofile.domain.DeviceProfilingService;
import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentDeviceProfile;
import com.tenx.fraudamlmanager.threatmetrix.infrastructure.deviceprofile.PaymentDeviceProfilingException;
import feign.FeignException;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ThreatmetrixDeviceProfilingServiceImpl implements DeviceProfilingService {

  private static final String THREATMETRIX_CLIENT_ERROR_WITH_PARTYKEY = "ThreatMetrix client error, partyKey: {}";
  private static final String SAVE_NEW_DEVICE_PROFILE_FROM_TMX_FOR_PARTY_KEY = "Save new Device Profile from TMX for Party Key {}";
  private static final String COULD_NOT_FIND_DEVICE_PROFILE_IN_DATABASE_CALLING_TMXA_FOR_PARTY_KEY = "Could not find Device profile in database, calling TMXA for PartyKey {}";
  private static final String CALLING_TMXA_FOR_SESSION_ID = "Calling TMXA for SessionId {}";
  private final DeviceProfilingEventRepository deviceProfilingEventRepository;

  private final ThreatMetrixAdapterClient threatMetrixAdapterClient;


  public void retrieveAndSaveDeviceProfilingEventData(DeviceProfilingEvent deviceProfilingEvent) {
    DeviceProfileEntity deviceProfilingEventEntity =
        DeviceProfilingEventEntityMapper.MAPPER.toDeviceProfilingEntity(
            deviceProfilingEvent, getThreatmetrixData(deviceProfilingEvent.getSessionId()));
    log.info(SAVE_NEW_DEVICE_PROFILE_FROM_TMX_FOR_PARTY_KEY, deviceProfilingEvent.getPartyKey());
    deviceProfilingEventRepository.save(deviceProfilingEventEntity);
  }

  private final DeviceProfileMetrics deviceProfileMetrics;

  public HashMap<String, Object> getThreatmetrixData(String sessionId) {
    log.info(CALLING_TMXA_FOR_SESSION_ID, sessionId);
    HashMap<String, Object> threatMetrixData = new HashMap<>();
    try {
      threatMetrixData = threatMetrixAdapterClient.getThreatmetrixData(sessionId);
      deviceProfileMetrics.incrementPaymentDeviceProfileUsingEventRequestsToTMXA();
      return threatMetrixData;
    } catch (FeignException e) {
      deviceProfileMetrics.incrementPaymentDeviceProfileUsingEventRequestsToTMXAFailed();
      return threatMetrixData;
    }

  }

  public PaymentDeviceProfile retrieveAndSaveDeviceProfilingEventDataUsingPartyKey(String partyKey)
      throws PaymentDeviceProfilingException {
    PaymentDeviceProfile paymentDeviceProfile = getThreatmetrixDataByPartyKey(partyKey);
    DeviceProfileEntity deviceProfileEntity = PaymentDeviceProfileToDeviceProfileEntityMapper.MAPPER
        .toDeviceProfileEntity(paymentDeviceProfile);
    if (deviceProfileEntity.getPartyKey() != null) {
      log.info(SAVE_NEW_DEVICE_PROFILE_FROM_TMX_FOR_PARTY_KEY, partyKey);
      deviceProfilingEventRepository.save(deviceProfileEntity);
    }
    return paymentDeviceProfile;
  }

  public PaymentDeviceProfile getThreatmetrixDataByPartyKey(String partyKey) throws PaymentDeviceProfilingException {
    PaymentDeviceProfile paymentDeviceProfile;
    log.info(COULD_NOT_FIND_DEVICE_PROFILE_IN_DATABASE_CALLING_TMXA_FOR_PARTY_KEY, partyKey);
    try {
      paymentDeviceProfile = threatMetrixAdapterClient.getThreatmetrixDataByPartyKey(partyKey);
      if (paymentDeviceProfile != null) {
        paymentDeviceProfile.setPartyKey(partyKey);
      }
      deviceProfileMetrics.incrementPaymentDeviceProfileUsingPartyKeyRequestsToTMXA();
      return paymentDeviceProfile;
    } catch (FeignException e) {
      deviceProfileMetrics.incrementPaymentDeviceProfileUsingPartyKeyRequestsToTMXAFailed();
      if (e.status() != 0 && !HttpStatus.valueOf(e.status()).equals(HttpStatus.NOT_FOUND)) {
        log.error(THREATMETRIX_CLIENT_ERROR_WITH_PARTYKEY, partyKey);
      }
      throw new PaymentDeviceProfilingException(e.status(),
          THREATMETRIX_CLIENT_ERROR_WITH_PARTYKEY, e);
    }
  }
}
