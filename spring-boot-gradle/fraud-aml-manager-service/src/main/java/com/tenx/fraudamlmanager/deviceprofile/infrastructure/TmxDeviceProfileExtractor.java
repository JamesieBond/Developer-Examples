package com.tenx.fraudamlmanager.deviceprofile.infrastructure;

import com.tenx.fraudamlmanager.deviceprofile.domain.DeviceProfileExtractor;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author James Spencer
 */
@Service
public class TmxDeviceProfileExtractor implements DeviceProfileExtractor {

  @Value("${threatmetrixadapter.fields}")
  private ArrayList<String> fields;

  public HashMap<String, Object> extractSpecificDeviceProfileData(HashMap<String, Object> deviceProfileData,
      String deviceKeyId) {
    HashMap<String, Object> deviceProfileHashMap = new HashMap<>();
    for (String field : fields) {
      if (field.equals("device_key_id")) {
        deviceProfileHashMap.put(field, deviceKeyId);
      } else {
        deviceProfileHashMap.put(field, deviceProfileData.get(field));
      }
    }
    return deviceProfileHashMap;
  }

}