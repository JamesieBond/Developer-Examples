package com.tenx.logging.logger;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.Slf4JLogger;
import com.tenx.logging.util.Properties;
import com.tenx.logging.model.DatabaseLogMarkers;
import com.tenx.logging.util.JDBCDatabaseName;
import com.tenx.logging.util.TemporalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.Objects.isNull;
import static net.logstash.logback.marker.Markers.appendFields;

public class DatabaseLogger extends Slf4JLogger {

  private static final String LOG_MESSAGE = "DATABASE_EVENT";
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public void logSQL(int connectionId, String now, long elapsed,
      Category category, String prepared, String sql, String url) {

    List<String> sqlToIgnore = Properties.getSqlToIgnore();
    if (isNull(sqlToIgnore) || (sqlToIgnore.size() > 0 && !sqlContainsIgnoreValues(prepared.toLowerCase(), sqlToIgnore))) {
      final DatabaseLogMarkers markers = DatabaseLogMarkers.builder()
              .start_time(TemporalUtils.toDateTimeString(Long.parseLong(now)))
              .response_time(elapsed)
              .resource_database_name(JDBCDatabaseName.getDatabaseName(url))
              .resource_database_query(prepared)
              .build();
      log.info(appendFields(markers), LOG_MESSAGE);
    }
  }

  private boolean sqlContainsIgnoreValues(String sql, List<String> sqlToIgnore) {
    return sqlToIgnore.stream().anyMatch(sql::contains);
  }
}
