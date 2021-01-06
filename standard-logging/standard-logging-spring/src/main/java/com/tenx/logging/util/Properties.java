package com.tenx.logging.util;

import static java.util.Collections.EMPTY_LIST;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "10xlogging")
public class Properties {

  private static List<String> sqlToIgnore;
  private static List<String> inboundWebFilterIncluding;
  private static List<Pattern> allowedPatterns;

  public static List<String> getSqlToIgnore() {
    return sqlToIgnore;
  }

  public void setSuppressForSqlContaining(List<String> sqlContaining) {
    sqlToIgnore = sqlContaining.stream().map(String::toLowerCase).collect(Collectors.toList());
  }

  public static List<String> getInboundWebFilterIncluding() {
    return inboundWebFilterIncluding;
  }

  public void setInboundWebFilterIncluding(List<String> inboundToInclude) {
    inboundWebFilterIncluding = inboundToInclude;
    allowedPatterns = !isEmpty(inboundWebFilterIncluding)
        ? inboundWebFilterIncluding.stream().filter(s -> s != null && !s.trim().isEmpty()).map(Pattern::compile).collect(toList())
        : EMPTY_LIST;
  }

  public static List<Pattern> getAllowedPatterns() {
    return allowedPatterns;
  }
}
