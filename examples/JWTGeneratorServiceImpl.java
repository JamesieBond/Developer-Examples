package com.tenx.feedzaimanager.infrastructure.feedzai.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author vstanciu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JWTGeneratorServiceImpl implements JWTGeneratorService, InitializingBean {

  private static final String ERROR_IN_SIGNING_THE_JWT = "Error in signing the JWT";
  private static final String BEARER = "Bearer ";
  private static final String JWT_CLAIMS_SET_BUILT = "JWT Claims set built {}";
  private static final String JWT_SIGNED = "JWT signed";
  private static final String NO_JWK_FOUND = "No JWK found";
  private static final String FEEDZAI_JWS_ENCODED_JWKSET_PROPERTY_NAME = "feedzai.jws.encodedJWKSet";
  private static final String JWK_KID_OBTAINED = "JWK kid obtained";

  @Value("${feedzai.jws.issuerName}")
  private String issuerName;

  @Value("${feedzai.jws.encodedJWKSet}")
  private String encodedJWKSet;

  private String jwkkid;

  private final JWSSigner jwsSigner;

  @Override
  public void afterPropertiesSet() throws Exception {
    String jwkSetString = new String(Base64.getDecoder().decode(encodedJWKSet.getBytes()));
    JWKSet jwkSet = JWKSet.parse(jwkSetString);
    if (jwkSet.getKeys() == null || jwkSet.getKeys().isEmpty()) {
      throw new InvalidPropertyException(JWTGeneratorServiceImpl.class, FEEDZAI_JWS_ENCODED_JWKSET_PROPERTY_NAME,
          NO_JWK_FOUND);
    }
    jwkkid = jwkSet.getKeys().get(0).getKeyID();
    log.info(JWK_KID_OBTAINED);
  }


  @Override
  public Optional<SignedJWT> generateSignedJWT() {
    SignedJWT signedJWT = new SignedJWT(getJWSHeader(), getJWTClaimsSet());
    try {
      signedJWT.sign(jwsSigner);
      log.info(JWT_SIGNED);
      return Optional.of(signedJWT);
    } catch (JOSEException ex) {
      log.error(ERROR_IN_SIGNING_THE_JWT);
      return Optional.empty();
    }
  }

  @Override
  public String getAuthorizationHeaderValue(SignedJWT signedJWT) {
    return BEARER + signedJWT.serialize();
  }

  private JWTClaimsSet getJWTClaimsSet() {
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .issuer(issuerName)
        .expirationTime(Date.from(Instant.now().plusSeconds(300)))
        .notBeforeTime(Date.from(Instant.now()))
        .jwtID(UUID.randomUUID().toString())
        .build();
    log.debug(JWT_CLAIMS_SET_BUILT, claimsSet.toString());
    return claimsSet;
  }

  private JWSHeader getJWSHeader() {
    return new JWSHeader.Builder(JWSAlgorithm.RS512)
        .keyID(jwkkid)
        .build();
  }
}
