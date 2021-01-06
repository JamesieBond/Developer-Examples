package com.tenx.fraudamlmanager.deviceprofile.infrastructure;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import com.tenx.fraudamlmanager.deviceprofile.domain.DeviceProfilingService;
import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentDeviceProfile;
import com.tenx.fraudamlmanager.threatmetrix.infrastructure.deviceprofile.PaymentDeviceProfilingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class DeviceProfileFetchDeviceKeyTest {

  private DeviceProfileFetchDeviceKey deviceProfileFetchDeviceKey;

  @MockBean
  private DeviceProfilingService deviceProfilingService;


  private final String DEVICE_ID = "deviceKeyId";
  private final String PARTY_KEY = "somePartyKey";

  @BeforeEach
  public void initialise() {
    this.deviceProfileFetchDeviceKey = new DeviceProfileFetchDeviceKey(deviceProfilingService);
  }


  @Test
  public void testFetchTMXADataUsingPartyKeySuccesfullWithNullInput() throws PaymentDeviceProfilingException {
    PaymentDeviceProfile inPaymentDeviceProfile = null;

    PaymentDeviceProfile outPaymentDeviceProfile = new PaymentDeviceProfile();
    outPaymentDeviceProfile.setDeviceKeyId(DEVICE_ID);
    outPaymentDeviceProfile.setPartyKey(PARTY_KEY);

    given(deviceProfilingService.
        retrieveAndSaveDeviceProfilingEventDataUsingPartyKey(PARTY_KEY))
        .willReturn(outPaymentDeviceProfile);

    PaymentDeviceProfile returnedPaymentDeviceProfie = deviceProfileFetchDeviceKey
        .fetchTMXADataUsingPartyKey(inPaymentDeviceProfile, PARTY_KEY);

    assertEquals(returnedPaymentDeviceProfie, outPaymentDeviceProfile);
    assertThat(returnedPaymentDeviceProfie.getDeviceKeyId(), is(DEVICE_ID));
    assertThat(returnedPaymentDeviceProfie.getPartyKey(), is(PARTY_KEY));

  }


  @Test
  public void testFetchTMXADataUsingPartyKeySuccesfullWithMissingKey() throws PaymentDeviceProfilingException {
    PaymentDeviceProfile inPaymentDeviceProfile = new PaymentDeviceProfile();
    inPaymentDeviceProfile.setPartyKey(PARTY_KEY);
    //device key id is missing

    PaymentDeviceProfile outPaymentDeviceProfile = new PaymentDeviceProfile();
    outPaymentDeviceProfile.setDeviceKeyId(DEVICE_ID);
    outPaymentDeviceProfile.setPartyKey(PARTY_KEY);

    given(deviceProfilingService.
        retrieveAndSaveDeviceProfilingEventDataUsingPartyKey(PARTY_KEY))
        .willReturn(outPaymentDeviceProfile);

    PaymentDeviceProfile returnedPaymentDeviceProfie = deviceProfileFetchDeviceKey
        .fetchTMXADataUsingPartyKey(inPaymentDeviceProfile, PARTY_KEY);

    assertEquals(returnedPaymentDeviceProfie, outPaymentDeviceProfile);
    assertThat(returnedPaymentDeviceProfie.getDeviceKeyId(), is(DEVICE_ID));
    assertThat(returnedPaymentDeviceProfie.getPartyKey(), is(PARTY_KEY));

  }


  @Test
  public void testFetchTMXADataUsingPartyKeySuccesfullWithPartialReturnFromService()
      throws PaymentDeviceProfilingException {
    PaymentDeviceProfile inPaymentDeviceProfile = new PaymentDeviceProfile();
    inPaymentDeviceProfile.setPartyKey(PARTY_KEY);

    PaymentDeviceProfile outPaymentDeviceProfile = new PaymentDeviceProfile();
    outPaymentDeviceProfile.setPartyKey(PARTY_KEY);
    // device key id missing

    given(deviceProfilingService.
        retrieveAndSaveDeviceProfilingEventDataUsingPartyKey(PARTY_KEY))
        .willReturn(outPaymentDeviceProfile);

    PaymentDeviceProfile returnedPaymentDeviceProfie = deviceProfileFetchDeviceKey
        .fetchTMXADataUsingPartyKey(inPaymentDeviceProfile, PARTY_KEY);

    assertThat(returnedPaymentDeviceProfie.getDeviceKeyId(), is("unknown"));
    assertTrue(returnedPaymentDeviceProfie.getDeviceProfile().isEmpty());

  }


  @Test
  public void testFetchTMXADataUsingPartyKeySuccesfullWithEmptyReturnFromService()
      throws PaymentDeviceProfilingException {
    PaymentDeviceProfile inPaymentDeviceProfile = new PaymentDeviceProfile();
    inPaymentDeviceProfile.setPartyKey(PARTY_KEY);

    PaymentDeviceProfile outPaymentDeviceProfile = null;

    given(deviceProfilingService.
        retrieveAndSaveDeviceProfilingEventDataUsingPartyKey(PARTY_KEY))
        .willReturn(outPaymentDeviceProfile);

    PaymentDeviceProfile returnedPaymentDeviceProfie = deviceProfileFetchDeviceKey
        .fetchTMXADataUsingPartyKey(inPaymentDeviceProfile, PARTY_KEY);

    assertThat(returnedPaymentDeviceProfie.getDeviceKeyId(), is("unknown"));
    assertTrue(returnedPaymentDeviceProfie.getDeviceProfile().isEmpty());

  }

  @Test
  public void testFetchTMXADataUsingPartyKeyWithException() throws PaymentDeviceProfilingException {
    PaymentDeviceProfile inPaymentDeviceProfile = new PaymentDeviceProfile();
    String partyKey = "some key";

    given(deviceProfilingService.
        retrieveAndSaveDeviceProfilingEventDataUsingPartyKey(partyKey))
        .willThrow(new PaymentDeviceProfilingException(500, "exception"));

    PaymentDeviceProfile outPaymentDeviceProfie = deviceProfileFetchDeviceKey
        .fetchTMXADataUsingPartyKey(inPaymentDeviceProfile, partyKey);

    assertThat(outPaymentDeviceProfie.getDeviceKeyId(), is("unknown"));
    assertTrue(outPaymentDeviceProfie.getDeviceProfile().isEmpty());
  }


}