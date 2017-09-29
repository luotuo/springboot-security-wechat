package com.jwcq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Date;

/**
 * Created by liuma on 2017/7/4.
 */
@Configuration
@EnableSwagger2
public class Swaggers {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jwcq.controller"))
                .build();
//        System.out.println("create rest api"+new Date());
//        Docket docket = new Docket(DocumentationType.SWAGGER_2);
//        docket.enable(true);
//          docket.apiInfo(apiInfo())
//                .select().paths(PathSelectors.any())
//                .build();
//        return docket;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("3Audit 稽查系统 API文档页面")
                .description("API调用查找页面")
                .termsOfServiceUrl("http://118.190.132.68:3000/qiaorong/3audit")
                .contact("3Audit")
                .version("1.0")
                .build();
    }

}
