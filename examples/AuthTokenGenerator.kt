package com.tenx.salesforceadapter.salesforce

import arrow.core.*
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.tenx.salesforceadapter.config.SalesforceProps
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.DefaultJwsHeader
import org.apache.commons.logging.LogFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*


@Component
class AuthTokenGenerator(
    val restTemplate: RestTemplate,
    val salesforceProps: SalesforceProps,
    val clock: java.time.Clock = java.time.Clock.systemUTC()
) {
    private val log = LogFactory.getLog(javaClass)
    private val authUri =
        UriComponentsBuilder.fromUriString(salesforceProps.baseUri).path(salesforceProps.authEndpoint).build().toUri()

    final fun get(): Option<String> = authorize(generateJwtToken())

    private fun generateJwtToken(): String {
        return Jwts.builder()
            .setSubject(salesforceProps.jwtSubject)
            .setIssuer(salesforceProps.jwtIssuer)
            .setAudience(salesforceProps.jwtAudience)
            .setExpiration(Date(clock.millis() + 1000))
            .signWith(io.jsonwebtoken.SignatureAlgorithm.RS256, getPrivateKey())
            .setHeader(DefaultJwsHeader().setAlgorithm("RS256"))
            .compact()
    }

    private fun getPrivateKey(): PrivateKey =
        KeyFactory
            .getInstance("RSA")
            .generatePrivate(PKCS8EncodedKeySpec(Base64.getDecoder().decode(salesforceProps.privateKey)))

    private fun authorize(token: String): Option<String> = Try {
        log.debug("Retrieving oauth2 token")
        restTemplate.exchange(
            authUri,
            HttpMethod.POST,
            HttpEntity(
                authorizationBody(token),
                authorizationHeaders()
            ),
            TokenResponse::class.java
        ).body?.access_token.orEmpty()
    }.getOrElse {
        log.error("Error while retrieving auth token: $it")
        return None
    }.toOption()

    private fun authorizationBody(token: String): MultiValueMap<String, String> =
        LinkedMultiValueMap<String, String>().apply {
            put("grant_type", Collections.singletonList("urn:ietf:params:oauth:grant-type:jwt-bearer"))
            put("assertion", Collections.singletonList(token))
        }

    private fun authorizationHeaders(): HttpHeaders =
        HttpHeaders().apply {
            put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(MediaType.MULTIPART_FORM_DATA_VALUE))
            put(HttpHeaders.ACCEPT, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE))
        }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class TokenResponse(
    val access_token: String? = null,
    val token_type: String? = null,
    val id_token: String? = null,
    val instance_url: String? = null,
    val id: String? = null
)