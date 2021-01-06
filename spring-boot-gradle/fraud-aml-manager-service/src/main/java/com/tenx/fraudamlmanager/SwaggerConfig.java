package com.tenx.fraudamlmanager;

import com.fasterxml.classmate.TypeResolver;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.ErrorDetails;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import({BeanValidatorPluginsConfiguration.class})
public class SwaggerConfig {

  @Autowired private TypeResolver typeResolver;

  @Bean
  public Docket api() {
    List<ResponseMessage> responseMessages =
        Arrays.asList(
            new ResponseMessageBuilder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Bad request")
                .responseModel(new ModelRef("ErrorDetails"))
                .build(),
            new ResponseMessageBuilder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Internal Server Error")
                .responseModel(new ModelRef("ErrorDetails"))
                .build());
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("Fraud and Financial crime")
        .tags(new Tag("payments", "Payments checks"))
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.tenx.fraudamlmanager"))
        .build()
        .securitySchemes(Collections.singletonList(apiKey()))
        .securityContexts(Collections.singletonList(securityContext()))
        .apiInfo(buildApiInfo())
        .additionalModels(typeResolver.resolve(ErrorDetails.class))
        .globalResponseMessage(RequestMethod.POST, responseMessages)
        .globalResponseMessage(RequestMethod.PUT, responseMessages)
        .globalResponseMessage(RequestMethod.GET, responseMessages)
        .globalResponseMessage(RequestMethod.DELETE, responseMessages);
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(defaultAuth())
        .forPaths(PathSelectors.any())
        .build();
  }

  private List<SecurityReference> defaultAuth() {
    final AuthorizationScope authorizationScope =
        new AuthorizationScope("global", "accessEverything");
    final AuthorizationScope[] authorizationScopes = new AuthorizationScope[] {authorizationScope};
    return Collections.singletonList(new SecurityReference("Bearer", authorizationScopes));
  }

  private ApiKey apiKey() {
    return new ApiKey("Bearer", "Authorization", "header");
  }

  private ApiInfo buildApiInfo() {
    return new ApiInfoBuilder().title("Fraud and AML Manager").version("2.9").build();
  }

  @Bean
  public UiConfiguration apiUI() {
    return UiConfigurationBuilder.builder().build();
  }
}
