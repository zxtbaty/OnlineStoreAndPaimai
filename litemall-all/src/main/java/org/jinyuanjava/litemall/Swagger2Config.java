package org.jinyuanjava.litemall;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Cash Zhang
 * @version v1.0
 * @since 2019/05/22 09:29
 * 访问方式  http://localhost:8090/swagger-ui.html#/
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
    // 定义分隔符
    private static final String splitor = ";";
    /**
     * swagger2的配置文件，这里可以配置swagger2的一些基本的内容，比如扫描的包等等
     *
     * @return Docket
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).
                apiInfo(apiInfo()).
                select()
                // 为当前包路径
                .apis(RequestHandlerSelectors.basePackage("org.jinyuanjava.litemall.admin.web"))
                .paths(PathSelectors.any())
                .build()
                .groupName("管理后台文档V1");
    }

    @Bean
    public Docket wap_api() {
        return new Docket(DocumentationType.SWAGGER_2).
                apiInfo(apiInfo()).
                select()
                // 为当前包路径
                .apis(RequestHandlerSelectors.basePackage("org.jinyuanjava.litemall.wx.web"))
                .paths(PathSelectors.any())
                .build()
                .groupName("WEB接口文档V1");
//        return new Docket(DocumentationType.SWAGGER_2).
//                select().
//                apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.ant("/web/**")).build().groupName("WEB接口文档V4.4").pathMapping("/")
//                .apiInfo(apiInfo("WEB接口文档V4.4及之前","文档中可以查询及测试接口调用参数和结果","4.4"));
    }

    @Bean
    public Docket out_api() {
        return new Docket(DocumentationType.SWAGGER_2).
                apiInfo(apiInfo()).
                select()
                // 为当前包路径
                .apis(RequestHandlerSelectors.basePackage("org.jinyuanjava.litemall.out.control.visited"))
                .paths(PathSelectors.any())
                .build()
                .groupName("为第三方提供接口文档V1");
    }

    /**
     * api文档的详细信息函数,注意这里的注解引用的是哪个
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // //大标题
                .title("商城后台 RESTful API")
                // 版本号
                .version("1.0")
//                .termsOfServiceUrl("NO terms of service")
                // 描述
                .description("API 描述")
                //作者
                .contact(new Contact("ZhangXiangTao", "https://blog.csdn.net/m0_37726449", "zxtbaty@163.com"))
//                .license("The Apache License, Version 2.0")
//                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .build();
    }



}
