package com.tenx.fraudamlmanager.paymentsv2.domestic.in.infrastructure;

import com.tenx.fraudamlmanager.infrastructure.RetryProperties;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringErrorDecoder;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Slf4j
@EnableConfigurationProperties(RetryProperties.class)
public class DomesticInTransactionMonitoringClientConfigV2 {

  public static final String CLIENT_NAME = "domestic-in-transaction-monitoring-v2";

  @Autowired
  private RetryProperties retryProperties;

  /**
   * @return Default retryer
   */
  @Bean
  public Retryer retryer() {

    RetryProperties.RetryConfig config = retryProperties.getConfig().get(CLIENT_NAME);
    return new Retryer.Default(config.getPeriod(), config.getMaxPeriod(), config.getMaxAttempts());
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return new TransactionMonitoringErrorDecoder();
  }
}
