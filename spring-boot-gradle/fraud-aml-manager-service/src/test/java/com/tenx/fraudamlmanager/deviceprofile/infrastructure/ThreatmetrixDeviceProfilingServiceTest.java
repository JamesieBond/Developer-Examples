package com.tenx.fraudamlmanager.deviceprofile.infrastructure;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraud.DeviceProfilingEvent;
import com.tenx.fraudamlmanager.deviceprofile.domain.DeviceProfileMetrics;
import com.tenx.fraudamlmanager.deviceprofile.domain.DeviceProfilingService;
import com.tenx.fraudamlmanager.threatmetrix.infrastructure.deviceprofile.PaymentDeviceProfilingException;
import feign.FeignException;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ThreatmetrixDeviceProfilingServiceTest {

  private static final String PARTYKEY = "tmx-00001";
  private static final String SESSIONID = "tmx-00002";
  private static final String DEVICEKEYID = "tmx-00003";


  private DeviceProfilingService deviceProfilingService;

  @MockBean
  private DeviceProfilingEventRepository deviceProfilingEventRepository;

  @MockBean
  private ThreatMetrixAdapterClient threatMetrixAdapterClient;

  @MockBean
  private DeviceProfileMetrics deviceProfileMetrics;


  private DeviceProfilingEvent deviceProfilingEvent;
  private DeviceProfileEntity deviceProfileEntity;
  private HashMap<String, Object> threatMetrixDeviceProfileResponse;


  @BeforeEach
  public void initialise() {
    this.deviceProfilingService =
        new ThreatmetrixDeviceProfilingServiceImpl(deviceProfilingEventRepository, threatMetrixAdapterClient,
            deviceProfileMetrics);
    threatMetrixDeviceProfileResponse = new HashMap<>();
    threatMetrixDeviceProfileResponse.put("agent_type", "browser_computer");
    threatMetrixDeviceProfileResponse.put("api_call_datetime", "2019-11-20 14:09:04.230");
    threatMetrixDeviceProfileResponse.put("api_version", "10.6");
    threatMetrixDeviceProfileResponse.put("bb_assessment", "275.00");
    threatMetrixDeviceProfileResponse.put("bb_bot_score", "500.00");
    threatMetrixDeviceProfileResponse.put("bb_fraud_score", "500.00");
    threatMetrixDeviceProfileResponse.put("browser", "Unknown");
    threatMetrixDeviceProfileResponse.put("browser_string", "PostmanRuntime/7.19.0");
    threatMetrixDeviceProfileResponse.put(
        "browser_string_hash", "a8bb48c2ce79fff6133fd1ca1c910ba4");
    threatMetrixDeviceProfileResponse.put("custom_match_5", "fa8b40fcfb2c835d1ddb22fd22326803");
    threatMetrixDeviceProfileResponse.put("custom_match_6", "2cc1a72b4a11a431f3d0384ac4bd2657");
    threatMetrixDeviceProfileResponse.put("device_first_seen", "2019-11-15");
    threatMetrixDeviceProfileResponse.put("device_id", "4bf1f54591a04c22b55b79e7cdc592f9");
    threatMetrixDeviceProfileResponse.put("device_id_confidence", "100");
    threatMetrixDeviceProfileResponse.put("device_last_event", "2019-11-20");
    threatMetrixDeviceProfileResponse.put("device_last_update", "2019-11-20");
    threatMetrixDeviceProfileResponse.put("device_match_result", "success");
    threatMetrixDeviceProfileResponse.put("device_result", "success");
    threatMetrixDeviceProfileResponse.put("device_score", "0");
    threatMetrixDeviceProfileResponse.put("device_worst_score", "0");
    threatMetrixDeviceProfileResponse.put("digital_id_result", "not_enough_attribs");
    threatMetrixDeviceProfileResponse.put("enabled_ck", "yes");
    threatMetrixDeviceProfileResponse.put("enabled_fl", "no");
    threatMetrixDeviceProfileResponse.put("enabled_im", "no");
    threatMetrixDeviceProfileResponse.put("enabled_js", "no");
    threatMetrixDeviceProfileResponse.put("event_datetime", "2019-11-20 14:09:04.230");
    threatMetrixDeviceProfileResponse.put("event_type", "payment");
    threatMetrixDeviceProfileResponse.put("fuzzy_device_first_seen", "2019-11-15");
    threatMetrixDeviceProfileResponse.put("fuzzy_device_id", "4bf1f54591a04c22b55b79e7cdc592f9");
    threatMetrixDeviceProfileResponse.put("fuzzy_device_id_confidence", "100.00");
    threatMetrixDeviceProfileResponse.put("fuzzy_device_last_event", "2019-11-20");
    threatMetrixDeviceProfileResponse.put("fuzzy_device_last_update", "2019-11-20");

    deviceProfileEntity = new DeviceProfileEntity();
    deviceProfileEntity.setDeviceProfile(threatMetrixDeviceProfileResponse);
    deviceProfileEntity.setPartyKey(PARTYKEY);
    deviceProfileEntity.setDeviceKeyId(DEVICEKEYID);
    deviceProfileEntity.setSessionId(SESSIONID);

    deviceProfilingEvent = DeviceProfilingEvent.newBuilder()
        .setDeviceKeyId("deviceKeyId")
        .setEventType("eventType")
        .setPartyKey("partyKey")
        .setSessionId("sessionId")
        .build();
  }


  @Test
  public void testretrieveAndSaveDeviceProfilingEventDataUsingPartyKeyWithException() {

    given(threatMetrixAdapterClient.getThreatmetrixDataByPartyKey(PARTYKEY))
        .willThrow(FeignException.class);

    assertThrows(PaymentDeviceProfilingException.class,
        () -> deviceProfilingService.retrieveAndSaveDeviceProfilingEventDataUsingPartyKey(PARTYKEY));

    verifyZeroInteractions(deviceProfilingEventRepository);

    verify(deviceProfileMetrics, times(1)).incrementPaymentDeviceProfileUsingPartyKeyRequestsToTMXAFailed();

  }


  @Test
  public void testFindDeviceProfileBySessionId() {
    given(deviceProfilingEventRepository.findByPartyKeyAndDeviceKeyId(PARTYKEY, DEVICEKEYID))
        .willReturn(deviceProfileEntity);
    given(threatMetrixAdapterClient.getThreatmetrixData(SESSIONID))
        .willReturn(threatMetrixDeviceProfileResponse);

    deviceProfilingService.retrieveAndSaveDeviceProfilingEventData(deviceProfilingEvent);
    verify(threatMetrixAdapterClient, times(1)).getThreatmetrixData(deviceProfilingEvent.getSessionId());
    verify(deviceProfileMetrics, times(1)).incrementPaymentDeviceProfileUsingEventRequestsToTMXA();
  }

  @Test
  public void testThreatMetrixResponse() {
    given(deviceProfilingEventRepository.findByPartyKeyAndDeviceKeyId(PARTYKEY, DEVICEKEYID))
        .willReturn(deviceProfileEntity);
    given(threatMetrixAdapterClient.getThreatmetrixData(SESSIONID))
        .willReturn(threatMetrixDeviceProfileResponse);

    HashMap<String, Object> deviceProfile = deviceProfilingService.getThreatmetrixData(SESSIONID);
    assertThat(deviceProfile.toString(), is(threatMetrixDeviceProfileResponse.toString()));
    verify(deviceProfileMetrics, times(1)).incrementPaymentDeviceProfileUsingEventRequestsToTMXA();
  }

  @Test
  public void testFindDeviceProfileWithException() {
    given(deviceProfilingEventRepository.findByPartyKeyAndDeviceKeyId(PARTYKEY, DEVICEKEYID))
        .willReturn(deviceProfileEntity);
    given(threatMetrixAdapterClient.getThreatmetrixData(deviceProfilingEvent.getSessionId()))
        .willThrow(FeignException.class);

    deviceProfilingService.retrieveAndSaveDeviceProfilingEventData(deviceProfilingEvent);
    verify(threatMetrixAdapterClient, times(1)).getThreatmetrixData(deviceProfilingEvent.getSessionId());
    verify(deviceProfileMetrics, times(1)).incrementPaymentDeviceProfileUsingEventRequestsToTMXAFailed();
  }

}
