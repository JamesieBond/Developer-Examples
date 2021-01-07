package com.tenx.feedzaimanager.payments.fraudcallback.api;

import com.auth0.jwk.JwkProvider;
import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity(debug = true)
public class FeedzaiSecuredEndpointConfiguration extends WebSecurityConfigurerAdapter {

  @Value(value = "${feedzai.jws.apiAudience}")
  private String apiAudience;

  @Value(value = "${feedzai.jws.issuedName}")
  private String issuer;

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
    configuration.setAllowCredentials(true);
    configuration.addAllowedHeader("Authorization");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors();
    JwtWebSecurityConfigurer
        .forRS256(apiAudience, issuer)
        .configure(http)
        .authorizeRequests()
        .antMatchers(HttpMethod.GET).permitAll()
        .antMatchers(HttpMethod.POST, "/v1/").permitAll()
        .antMatchers(HttpMethod.POST, "/external/case/update").authenticated();
  }
}