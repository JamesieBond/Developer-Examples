package com.tenx.logging.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DatabaseLogMarkers {
    private String start_time;
    private long response_time;
    private String resource_database_name;
    private String resource_database_query;
}
