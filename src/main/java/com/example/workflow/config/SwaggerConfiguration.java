package com.example.workflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
//                .apiInfo(metaData())
//                .tags(new Tag("apiInteg", "Интеграционный сервис"),
//                      new Tag("async", "Сервис асинхронного вызова методов"),
//                      new Tag("AppUnitExecute", "Методы обработки модулей enUnit"),
//                      new Tag("common-execute", "Сервис выполнения методов"),
//                      new Tag("dbexecute", "Сервис взаимодействия с объектами базы данных"),
//                      new Tag("enentity", "Сервис взаимодействия с сущностями словарной системы"),
//                      new Tag("lock", "Сервис блокировок объектов словарной системы"),
//                      new Tag("provider", "Сервис взаимодействия с объектами сущностей словарной системы"),
//                      new Tag("setting", "Сервис взаимодействия с глобальными и локальными параметрами приложения"),
//                      new Tag("auth", "Сервис авторизации и взаимодействия с сессиями пользователей"),
//                      new Tag("ldap", "Сервис взаимодействия с LDAP каталогом"),
//                      new Tag("management", "Сервис управления сессиями и блокировками"),
//                      new Tag("protection", "Сервис защиты кода"),
//                      new Tag("sheduler", "Сервис планировщика задач"),
//                      new Tag("sltman", "Сервис SltMan"),
//                      new Tag("tracer", "Сервис трассировщика сервера приложений"),
//                      new Tag("transaction", "Сервис управления транзакциями"),
//                      new Tag("updater", "Сервис модуля updater")
//                )
                ;
    }

//    private ApiInfo metaData() {
//        return new ApiInfoBuilder()
//                .title("REST API для платформы MeNext")
//                .description("Spring Boot REST API for MeNext Server")
//                .version("1.0.2")
//                .contact(new Contact("Diasoft", "http://www.diasoft.ru", "info@diasoft.ru"))
//                .build();
//    }
}
