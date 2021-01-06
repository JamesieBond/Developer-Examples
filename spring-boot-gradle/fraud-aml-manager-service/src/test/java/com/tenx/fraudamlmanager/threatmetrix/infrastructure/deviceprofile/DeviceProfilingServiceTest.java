package com.tenx.fraudamlmanager.threatmetrix.infrastructure.deviceprofile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraud.DeviceProfilingEvent;
import com.tenx.fraudamlmanager.deviceprofile.domain.DeviceProfileExtractor;
import com.tenx.fraudamlmanager.deviceprofile.domain.DeviceProfileService;
import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentDeviceProfile;
import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentsDeviceProfileService;
import com.tenx.fraudamlmanager.deviceprofile.infrastructure.DeviceProfileEntity;
import com.tenx.fraudamlmanager.deviceprofile.infrastructure.DeviceProfileFetchDeviceKey;
import com.tenx.fraudamlmanager.deviceprofile.infrastructure.PaymentsDeviceProfileServiceImpl;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DeviceProfilingServiceTest {

    private PaymentsDeviceProfileService paymentsDeviceProfileService;

    @MockBean
    private DeviceProfileService deviceProfileService;

    @MockBean
    private DeviceProfileExtractor deviceProfileExtractor;

    @MockBean
    private DeviceProfileFetchDeviceKey deviceProfileFetchDeviceKey;

    @BeforeEach
    public void initialise() {
        this.paymentsDeviceProfileService = new PaymentsDeviceProfileServiceImpl(
            deviceProfileService, deviceProfileExtractor, deviceProfileFetchDeviceKey);
    }

    @Test
    public void testExtractDeviceProfile() throws Exception {
        String partyKey = "tmx-00001";
        String sessionId = "tmx-00002";
        String deviceKeyId = "tmx-00003";

        HashMap<String, Object> threatMetrixDeviceProfileResponse = new HashMap<String, Object>();
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
        threatMetrixDeviceProfileResponse.put("time_zone", "2019-11-20 14:09:04.230");

        PaymentDeviceProfile paymentDeviceProfile = new PaymentDeviceProfile();
        paymentDeviceProfile.setDeviceKeyId(deviceKeyId);
        paymentDeviceProfile.setPartyKey(partyKey);
        paymentDeviceProfile.setDeviceProfile(threatMetrixDeviceProfileResponse);

        DeviceProfileEntity deviceProfileEntity = new DeviceProfileEntity();
        deviceProfileEntity.setDeviceProfile(threatMetrixDeviceProfileResponse);
        deviceProfileEntity.setPartyKey(partyKey);
        deviceProfileEntity.setDeviceKeyId(deviceKeyId);
        deviceProfileEntity.setSessionId(sessionId);
        DeviceProfilingEvent deviceProfilingEvent =
            DeviceProfilingEvent.newBuilder()
                .setDeviceKeyId("deviceKeyId")
                .setEventType("eventType")
                .setPartyKey("partyKey")
                .setSessionId("sessionId")
                .build();

        given(deviceProfileService.retrieveDeviceProfileWith(partyKey, null)).willReturn(deviceProfileEntity);
        given(deviceProfileFetchDeviceKey.fetchTMXADataUsingPartyKey(any(), eq(partyKey)))
                .willReturn(paymentDeviceProfile);
        given(deviceProfileExtractor.extractSpecificDeviceProfileData(any(), any()))
            .willReturn(threatMetrixDeviceProfileResponse);

        HashMap<String, Object> deviceProfileResult = paymentsDeviceProfileService
            .getThreatmetrixDataForPayment(partyKey, any());

        assertThat(deviceProfileResult.get("time_zone"), is(threatMetrixDeviceProfileResponse.get("time_zone")));
        Mockito.verify(deviceProfileService, times(1)).retrieveDeviceProfileWith(any());
        Mockito.verify(deviceProfileExtractor, times(1)).extractSpecificDeviceProfileData(any(), any());
        Mockito.verify(deviceProfileFetchDeviceKey, times(1)).fetchTMXADataUsingPartyKey(any(), any());

    }
}
