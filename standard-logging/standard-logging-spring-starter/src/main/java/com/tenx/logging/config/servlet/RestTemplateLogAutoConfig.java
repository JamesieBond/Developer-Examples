package com.tenx.logging.config.servlet;

import com.tenx.logging.interceptor.RestTemplateInterceptor;
import com.tenx.logging.logger.OutboundLogger;
import com.tenx.logging.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(Properties.class)
@ComponentScan({"com.tenx.logging.*"})
@ConditionalOnBean({RestTemplate.class, OutboundLogger.class})
public class RestTemplateLogAutoConfig {

  @Autowired(required = false)
  private List<RestTemplate> templates;

  @Autowired
  private RestTemplateInterceptor restTemplateInterceptor;

  @PostConstruct
  public void addRestTemplateInterceptor() {
    templates.forEach(template ->{
      List<ClientHttpRequestInterceptor> interceptorList = new ArrayList<>(template.getInterceptors());
      interceptorList.add(restTemplateInterceptor);
      template.setInterceptors(interceptorList);
    });
  }
}
