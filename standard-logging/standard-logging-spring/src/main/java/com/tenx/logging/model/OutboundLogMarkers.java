package com.tenx.logging.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OutboundLogMarkers {
    private String start_time;
    private long response_time;
    private String http_request_method;
    private String http_request_host;
    private String http_request_path;
    private int http_response_code;
}