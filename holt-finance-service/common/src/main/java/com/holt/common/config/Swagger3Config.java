package com.holt.common.config;

import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Swagger配置
 */
@Configuration
@ConditionalOnBean(SwaggerProperties.class)
/** OpenAPI */
@EnableOpenApi
@Slf4j
public class Swagger3Config implements WebMvcConfigurer {

    private final SwaggerProperties swaggerProperties;

    public Swagger3Config(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    public Docket customDocket() {
        log.info("swaggerProperties: {}", swaggerProperties);
        return new Docket(DocumentationType.OAS_30)
                // 是否开启Swagger
                .enable(swaggerProperties.getEnable())
                // 将api的元信息设置为包含在JSON ResourceListing响应中
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .protocols(newHashSet("https", "http"))
                .globalRequestParameters(getGlobalRequestParameters())
                .pathMapping(swaggerProperties.getPathMapping());
    }

    /**
     * API页面上半部分展示信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(swaggerProperties.getName())
                .description(swaggerProperties.getDescription())
                .version("Application Version: " + swaggerProperties.getVersion() + ", Spring Boot Version: " + SpringBootVersion.getVersion())
                .build();
    }

    /**
     * 设置授权信息
     */
    private List<SecurityScheme> securitySchemes() {
        ApiKey apiKey = new ApiKey("BASE_TOKEN", "token", In.HEADER.toValue());
        return Collections.singletonList(apiKey);
    }

    /**
     * 授权信息全局应用
     */
    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(Collections.singletonList(new SecurityReference("BASE_TOKEN", new AuthorizationScope[]{new AuthorizationScope("global", "")})))
                        .build()
        );
    }

    @SafeVarargs
    private final <T> Set<T> newHashSet(T... ts) {
        if (ts.length > 0) {
            return new LinkedHashSet<>(Arrays.asList(ts));
        }
        return null;
    }

    /**
     * 通用拦截器排除swagger设置，所有拦截器都会自动将swagger相关的资源排除信息
     */
    @SuppressWarnings("unchecked")
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        try {
            Field registrationsField = FieldUtils.getField(InterceptorRegistry.class, "registrations", true);
            List<InterceptorRegistration> registrations = (List<InterceptorRegistration>) ReflectionUtils.getField(registrationsField, registry);
            if (registrations != null) {
                for (InterceptorRegistration interceptorRegistration : registrations) {
                    interceptorRegistration
                            .excludePathPatterns("/swagger**/**")
                            .excludePathPatterns("/webjars/**")
                            .excludePathPatterns("/v3/**")
                            .excludePathPatterns("/doc.html");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 封装全局通用参数
     */
    private List<RequestParameter> getGlobalRequestParameters() {
        List<RequestParameter> parameters = new ArrayList<>();
//        parameters.add(new RequestParameterBuilder()
//                .name(CommonConstant.API_AUTH_KEY_NONCE)
//                .description("随机数")
//                .required(false)
//                .in(ParameterType.HEADER)
//                .build());
//        parameters.add(new RequestParameterBuilder()
//                .name(CommonConstant.API_AUTH_KEY_TIMESTAMP)
//                .description("时间戳")
//                .required(false)
//                .in(ParameterType.HEADER)
//                .build());
//        parameters.add(new RequestParameterBuilder()
//                .name(CommonConstant.API_AUTH_KEY_SIGNATURE)
//                .description("签名")
//                .required(false)
//                .in(ParameterType.HEADER)
//                .build());
//        parameters.add(new RequestParameterBuilder()
//                .name(CommonConstant.KEY_API_ACCESS_TOKEN)
//                .description("登录凭证")
//                .required(false)
//                .in(ParameterType.HEADER)
//                .build());
        return parameters;
    }
}
