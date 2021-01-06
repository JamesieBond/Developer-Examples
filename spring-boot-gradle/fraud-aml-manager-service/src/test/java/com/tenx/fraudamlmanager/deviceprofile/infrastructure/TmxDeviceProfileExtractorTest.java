package com.tenx.fraudamlmanager.deviceprofile.infrastructure;

import static java.util.Map.entry;

import com.tenx.fraudamlmanager.SpringBootTestBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TmxDeviceProfileExtractorTest extends SpringBootTestBase {

  @Autowired
  private TmxDeviceProfileExtractor extractor;

  private Map<String, Object> fullResponse = Map
      .ofEntries(entry("digital_id_result", "not_enough_attribs"), entry("policy_engine_version", "11.4.2"),
          entry("session_id_query_count", "19"), entry("bb_bot_score", "500.00"), entry("bb_fraud_score", "500.00"),
          entry("review_status", "pass"), entry("risk_rating", "low"),
          entry("rid", "71467099"),
          entry("{pvid", "1197991"),
          entry("score", "0}"), entry("{reason_code", "No Device ID"),
          entry("id", "0"), entry("type", "champion}]}"),
          entry("event_type", "login"), entry("request_duration", "9"),
          entry("event_datetime", "2020-09-04 09:41:05.640"),
          entry("champion_request_duration", "3"),
          entry("remote_access_score_var", "0"), entry("se_score_var", "0"), entry("tab", "0}"),
          entry("policy", "default"), entry("unknown_session", "yes"), entry("tmx_risk_rating", "neutral"),
          entry("session_id", "string"),
          entry("summary_risk_score", "-5"), entry("api_call_datetime", "2020-09-04 09:41:05.640"),
          entry("api_version", "11.4.1"), entry("request_result", "success"), entry("policy_score", "-5"),
          entry("service_type", "session-policy"), entry("org_id", "5jewu930"), entry("bb_assessment", "275.00"),
          entry("request_id", "c95b41ea-f02e-4694-af92-02ab9070706c"));


  @Test
  void extractSpecificDeviceProfileDataDefault() {
    HashMap<String, Object> result = extractor
        .extractSpecificDeviceProfileData(new HashMap<>(fullResponse), "deviceKey123");

    HashMap<String, Object> expected = new HashMap<>();
    expected.put("policy_score", "-5");
    expected.put("agent_health_status", null);
    expected.put("dns_ip_longitude", null);
    expected.put("event_type", "login");
    expected.put("device_id", null);
    expected.put("dns_ip_latitude", null);
    expected.put("jb_root", null);
    expected.put("device_first_seen", null);
    expected.put("session_id", "string");
    expected.put("time_zone", null);
    expected.put("dns_ip_geo", null);
    expected.put("device_key_id", "deviceKey123");

    Assertions.assertEquals(expected, result);
  }


  @Test
  void extractSpecificDeviceProfileDataJustOne() {

    ReflectionTestUtils.setField(extractor, "fields", new ArrayList<>(List.of("api_call_datetime")));

    HashMap<String, Object> result = extractor
        .extractSpecificDeviceProfileData(new HashMap<>(fullResponse), "deviceKey123");

    HashMap<String, Object> expected = new HashMap<>();
    expected.put("api_call_datetime", "2020-09-04 09:41:05.640");

    Assertions.assertEquals(expected, result);
  }
}