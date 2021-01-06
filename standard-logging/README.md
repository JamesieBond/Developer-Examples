[![Build Status](https://jenkins.10x.mylti3gh7p4x.net/buildStatus/icon?job=release_jar_modules%2Fstandard-logging-pipeline%2Fmaster)](https://jenkins.10x.mylti3gh7p4x.net/buildStatus/text?job=release_jar_modules%2Fstandard-logging-pipeline%2Fmaster)


## Overview
This library implements the logging standards outlined here: https://confluence.10x.mylti3gh7p4x.net/display/TECH/General+Logging+Standards

**Note - the library is currently a work in progress.**
**If you wish to collaborate, please join channel 10x Logging Standards - Library webex channel. Thank you!**

Log messages are grouped into the following categories:
- **Common log entry**. Includes the fields that will be common to ALL log entries. The `message` field will contain whatever information is output during your call to the `info`,`debug` etc. method of the logger in your code. Message are protected against attacks on log forging entries, by submitting CRLF characters.
- **Database log entry**. Is appended to the Common log entry whenever a database access event (`select` or DML statement) occurs. The `message` field will contain the value `"DATABASE_EVENT"`. These log events to be optionally switched on as and when required.
- **Inbound log entry**. Is appended to the Common log entry whenever an incoming message has been processed and responded to on endpoints within the service. The `message` field will contain the value `"INBOUND_EVENT"`. NOTE: The library currently supports inbound calls using Servlet or Reactive spring web applications. These log events to be optionally switched on as and when required.
- **Outbound log entry**. Is appended to the Common log entry whenever outbound messages are sent from the service. The `message` field will contain the value `"OUTBOUND_EVENT"`. NOTE: The library currently supports outbound calls using RestTemplate, Open-Feign and WebClient (Reactive). These log events to be optionally switched on as and when required.

## How to use this lib
- Pre-requisites
    - The library has dependencies on `com.github.gavlyukovskiy:p6spy-spring-boot-starter` and `net.logstash.logback:logstash-logback-encoder`, so these will automatically be included in your project as transitive dependencies. Hence, they can be removed from your projects dependencies, unless you need to use a more updated version of them. If you chose to declare these libraries in your project you will need:
        - `1.6.2` of `com.github.gavlyukovskiy:p6spy-spring-boot-starter`
        - `6.0` of `net.logstash.logback:logstash-logback-encoder`
    - Please ensure that Gradle plugin `com.gorylenko.gradle-git-properties` is enabled, so application version can be included by accessing the git.properties resource.
    - Please ensure that exact property `spring.application.name` is set to the name of your service.

- Project Dependency - what will get enabled:
    * Adding a dependency to the `standard-logging-spring-starter` jar will enable auto-configuration of all Logging capabilities within your service:
    
            implementation "com.tenx.logging:standard-logging-spring-starter:0.0.7-SNAPSHOT"

- In your service, modify your `logback-spring.xml` to include the pre-formatted appender:

```
<include resource="10xlogging-appender.xml"/>
```

If applicable ensure the root logger (plus any other loggers you want to be output in the standard format) references the appender `10XSTANDARD`:
```
<root level="INFO">
  <appender-ref ref="10XSTANDARD"/>
</root>
```

- Finally, by default the loggers provided in the library are switched OFF - you can switch on the individual loggers by setting the values in the application.properties/yaml file (NOTE: all loggers use INFO level)
```
logging.level.com.tenx.logging.logger.DatabaseLogger: INFO
logging.level.com.tenx.logging.logger.InboundLogger: INFO
logging.level.com.tenx.logging.logger.OutboundLogger: INFO
```
When switched OFF, the loggers will not be present at runtime (they are conditional on these properties), so performance footprint is avoided.

#### DATABASE LOGGER NOTES

By default, all database logging is disabled. 
To enabled it, set `logging.level.com.tenx.logging.logger.DatabaseLogger: INFO` on your properties file or as an environment variable.

- The database logger will fire on every database access event your service executes - this could be problematic if, say, you are using the message-outbox library that polls DB tables frequently...this could make the logs explode!
    To combat this, you can specify in your application properties file one or more strings that, if contained within the SQL being executed, will suppress the log message:
    
```
10xlogging:
  suppressForSqlContaining:
    - "scheduled_tasks"
    - "message_outbox"
  inboundWebFilterIncluding:
    - "^/ledger-manager/v\d+/fps/*"
    - "^/v\d+/*"
```
You may still experience some DATABASE_EVENT output on the logs during startup of the service due to database access occurring during initialisation. Once the service has started, any suppression detailed in the properties will take effect

#### OUTBOUND LOGGER NOTES

By default, all logging client bellow are disabled. 
To enabled them, set `logging.level.com.tenx.logging.logger.OutboundLogger: INFO` on your properties file or as an environment variable.

##### REST TEMPLATE CLIENT

No additional configuration is required for RestTemplate clients. 
If your service makes use of RestTemplate clients, an interceptor for all your outbound calls will be automatically setup.

##### FEIGN CLIENT

Logging of request/response data from the Feign client is included in the library. 
To enable this there are two ways:

- If your code defines clients using the `@FeignClient` annotation with interface, then no additional configuration is required.
By default, you should enable the `feign.okhttp.OkHttpClient` by declaring a Client Bean using Spring Configuration or simply in your application.properties `feign.okhttp.enabled=true`.

- If your code defines clients using `Resilience4jFeign.builder()`, then you will need to specifically inject the logging OpenFeign client bean.
This is due to the fact that `Resilience4jFeign.builder()` doesn't use bean injection and instantiates a default version of the OpenFeign client on it's builder, which can be overridden:

```
import feign.Client;

@Autowired
private Client client;

...
return Resilience4jFeign.builder(decorators)
    .client(client)
    .encoder(feignEncoder)
    .decoder(feignDecoder)
    ...
```

##### WEBCLIENT (REACTIVE)

No additional configuration is required for reactive use of WebClient. 
If your service makes use of Webclient, an `ExchangeFilter` for all your outbound calls will be automatically setup.

#### INBOUND LOGGER NOTES

By default, all logging client bellow are disabled. 
To enabled them, set `logging.level.com.tenx.logging.logger.InboundLogger: INFO` on your properties file or as an environment variable.

##### SERVLET FILTER

No additional configuration is required for Servlet applications. 
If your service makes use of non-reactive spring servlet, a `OncePerRequestFilter` for all your inbound calls will be automatically setup.

##### REACTIVE FILTER

No additional configuration is required for Reactive applications. 
If your service makes use of reactive WebFlux, a `WebFilter` for all your inbound calls will be automatically setup.


#### PII protection
Ideally all services should ensure no PII data can leak via logging, however an additional potential PII data masking capability can be enabled as a safety net, by adding the following in your application properties
`10xlogging.maskPII: true`  

The library will filter the messages that could potentially contain PII data by matching against a regular expression (the expression itself can be found inside com.tenx.logging.util.PIIConverter)  

In case the expression fails to capture the messages in your service you can append an extension regexp pattern that will be searched for and removed by using the application property `10xlogging.extensionPIIPattern`

Example - the below extension pattern will remove any messages that contain the words 'info1' or 'info2'
```
10xlogging:
  maskPII: true
  extensionPIIPattern: "(info1|info2)"
```



## How to develop locally
- When you're working on this library and want to test the changes integrated on a project then you should include the following:
    * Adding a dependency to the `standard-logging-spring` jar will include all the Common Logging capabilities, as well as the additional Database, Inbound and Outbound Loggers:
        
        implementation files('<path to standard-logging folder>/standard-logging-spring/build/libs/standard-logging-spring-<version>.jar')
        
    * Adding a dependency to the `standard-logging-spring-starter` jar will enable auto-configuration of all Logging capabilities within your service:
    
        implementation files('<path to standard-logging folder>/standard-logging-spring-starter/build/libs/standard-logging-spring-starter-<version>.jar')
