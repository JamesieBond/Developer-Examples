package com.tenx.logging.config.common;

import com.tenx.logging.util.Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Properties.class)
@ComponentScan({"com.tenx.logging.*"})
public class CommonLogAutoConfig {

}
