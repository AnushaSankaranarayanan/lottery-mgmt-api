package com.example.lotterymgmtapi.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;
import static com.google.common.base.Predicates.or;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket lotterryApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("public-api")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.lotterymgmtapi.controller"))
                .paths(getPaths())
                .build();
    }

    private Predicate<String> getPaths() {
        return or(regex("/lotteryapi/v1/ticket.*"), regex("/lotteryapi/v1/status.*"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Lottery Management API")
                .description("REST Interface that implements a simple lottery system").version("1.0").build();
    }
}