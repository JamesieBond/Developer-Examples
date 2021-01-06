package com.tenx.logging.config.servlet;

import com.tenx.logging.interceptor.FeignClientAspect;
import com.tenx.logging.logger.OutboundLogger;
import com.tenx.logging.util.Properties;
import com.tenx.logging.util.TemporalUtils;
import feign.Client;
import feign.okhttp.OkHttpClient;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Client.class)
@EnableConfigurationProperties(Properties.class)
@ComponentScan({"com.tenx.logging.*"})
@ConditionalOnBean({OutboundLogger.class})
public class FeignClientLogAutoConfig {

  @Autowired(required = false)
  private OutboundLogger outboundLogger;

  @Autowired
  private TemporalUtils temporalUtils;

  @Bean
  FeignClientAspect feignClientAspect(BeanFactory beanFactory) {
    return new FeignClientAspect(beanFactory, outboundLogger, temporalUtils);
  }

  @Bean
  @ConditionalOnMissingBean(Client.class)
  public Client feignClient() {
    return new OkHttpClient();
  }

}
