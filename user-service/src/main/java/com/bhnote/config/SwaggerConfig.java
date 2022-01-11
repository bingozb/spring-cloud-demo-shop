package com.bhnote.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author bingo
 * @date 2022/1/7
 */
@Component
@EnableOpenApi
@Data
public class SwaggerConfig {

    @Bean
    public Docket webApiDoc() {

        return new Docket(DocumentationType.OAS_30)
                .groupName("user-service")
                .pathMapping("/")
                // 定义是否开启swagger，false为关闭，可以通过变量控制，线上关闭
                .enable(true)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.bhnote"))
                // 正则匹配请求路径，并分配至当前分组
                .paths(PathSelectors.ant("/api/**"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts())
                .globalRequestParameters(getGlobalRequestParameters());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Cloud Alibaba Demo Shop")
                .description("微服务项目接口文档")
                .contact(new Contact("Bingo", "https://www.bhnote.com", "bingov5@icloud.com"))
                .version("v1.0")
                .build();
    }

    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(SecurityContext.builder()
                .securityReferences(Collections.singletonList(SecurityReference.builder()
                        .reference("token")
                        .scopes(new AuthorizationScope[]{new AuthorizationScope("global", "User Access Token")})
                        .build()))
                .build());
    }

    private List<SecurityScheme> securitySchemes() {
        return Collections.singletonList(new ApiKey("token", "token", "header"));
    }

    /**
     * 全局通用参数
     */
    private List<RequestParameter> getGlobalRequestParameters() {
        List<RequestParameter> parameters = new ArrayList<>();
        parameters.add(new RequestParameterBuilder()
                .name("token")
                .description("登录令牌")
                .in(ParameterType.HEADER)
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(false)
                .build());
        return parameters;
    }
}
