package com.tenx.logging.util;

import java.net.MalformedURLException;
import java.net.URL;

public final class JDBCDatabaseName {

    public static String getDatabaseName(String connectString) {
        try {
            String sanitizedString = null;
            int schemeEndOffset = connectString.indexOf("://");
            if (-1 == schemeEndOffset) {
                sanitizedString = "http://" + connectString;
            } else {
                sanitizedString = "http" + connectString.substring(schemeEndOffset);
            }

            URL connectUrl = new URL(sanitizedString);
            String databaseName = connectUrl.getPath();
            if (null == databaseName) {
                return connectString;
            }

            while (databaseName.startsWith("/")) {
                databaseName = databaseName.substring(1);
            }

            return databaseName;
        } catch (MalformedURLException mue) {
            return connectString;
        }
    }
}