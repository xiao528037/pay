package com.xiao.pay.paywechat.config;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-25 17:42:44
 * @description
 */
@Configuration
public class Swagger3Config {
    @Value("${swagger.enabled}")
    private boolean enabled;

    @Bean("skyTrainApi")
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(enabled)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("支付")
                .contact(new Contact("xiao.com", "", "aaaa@qq.com"))
                .version("v1.0")
                .description("支付")
                .build();
    }
}
