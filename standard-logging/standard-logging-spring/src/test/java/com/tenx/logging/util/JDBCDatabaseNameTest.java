package com.tenx.logging.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class JDBCDatabaseNameTest {

  @Test
  void connectionStringCorrupt() {
    String inputConn = "jdbc:postgresql:localhost:26257";
    String result = JDBCDatabaseName.getDatabaseName(inputConn);
    assertThat(result).isEqualTo(inputConn);
  }

  @Test
  void connectionsStringSslDisable() {
    String inputConn = "jdbc:postgresql://localhost:26257/testdb?sslmode=disable";
    String result = JDBCDatabaseName.getDatabaseName(inputConn);
    assertThat(result).isEqualTo("testdb");
  }

  @Test
  void connectionsStringSslDisableWithoutPort() {
    String inputConn = "jdbc:postgresql://localhost/testdb";
    String result = JDBCDatabaseName.getDatabaseName(inputConn);
    assertThat(result).isEqualTo("testdb");
  }

  @Test
  void connectionStringSslEnabled() {
    String inputConn = "jdbc:postgresql://cockroach.db.svc.cluster.local:26257/ledgerdb?sslmode=verify-full&sslcert=/cockroach-certs/client.dbusername.crt&sslkey=/cockroach-certs/client.dbusername.pk8&sslrootcert=/cockroach-certs/ca.crt&application_name=vasrails";
    String result = JDBCDatabaseName.getDatabaseName(inputConn);
    assertThat(result).isEqualTo("ledgerdb");
  }
}
