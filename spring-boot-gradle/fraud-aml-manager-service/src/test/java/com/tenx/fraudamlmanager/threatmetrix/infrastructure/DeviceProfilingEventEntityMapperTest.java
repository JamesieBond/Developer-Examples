package com.tenx.fraudamlmanager.threatmetrix.infrastructure;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.tenx.fraud.DeviceProfilingEvent;
import com.tenx.fraudamlmanager.deviceprofile.api.DeviceProfilingEventEntityMapper;
import com.tenx.fraudamlmanager.deviceprofile.infrastructure.DeviceProfileEntity;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class DeviceProfilingEventEntityMapperTest {
    @Test
    public void testToDeviceProfileEntity() throws Exception {
        DeviceProfilingEvent deviceProfilingEvent =
                DeviceProfilingEvent.newBuilder()
                        .setDeviceKeyId("deviceKeyId")
                        .setEventType("eventType")
                        .setPartyKey("partyKey")
                        .setSessionId("sessionId")
                        .build();

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

        DeviceProfileEntity deviceProfileEntity =
                DeviceProfilingEventEntityMapper.MAPPER.toDeviceProfilingEntity(
                        deviceProfilingEvent, threatMetrixDeviceProfileResponse);
        assertThat(deviceProfileEntity.getPartyKey(), is("partyKey"));
        assertThat(deviceProfileEntity.getDeviceKeyId(), is("deviceKeyId"));
        assertThat(deviceProfileEntity.getSessionId(), is("sessionId"));
        assertThat(
                deviceProfileEntity.getDeviceProfile().toString(),
                is(threatMetrixDeviceProfileResponse.toString()));
    }
}
