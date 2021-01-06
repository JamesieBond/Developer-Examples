package com.tenx.logging.config.common;

import static java.util.Objects.isNull;

import com.tenx.logging.logger.DatabaseLogger;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

public class P6SpyLogAutoConfig implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  private final Logger databaseLogger = LoggerFactory.getLogger(DatabaseLogger.class);

  public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    ConfigurableEnvironment environment = event.getEnvironment();
    if (!isNull(environment)) {
      configureP6SpyLogging(environment, databaseLogger.isInfoEnabled());
    }
  }

  private void configureP6SpyLogging(ConfigurableEnvironment environment, boolean loggingEnabled) {
    Properties p6spy = new Properties();
    p6spy.put("decorator.datasource.p6spy.enable-logging", String.valueOf(loggingEnabled));
    if (loggingEnabled) {
      p6spy.put("decorator.datasource.p6spy.logging", "custom");
      p6spy.put("decorator.datasource.p6spy.custom-appender-class", DatabaseLogger.class.getCanonicalName());
    }
    environment.getPropertySources().addFirst(new PropertiesPropertySource("p6LogDefault", p6spy));
  }
}
