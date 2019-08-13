package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("config")
public class ConfigController {

    @Value("${app.mandatory.property.title}") // if this property is not defined, the app won't start
    private String appTitle;

    @Value("${spring.profiles.active:No Spring profiles set.}") // with explicit default value "No Spring profiles set."
    private String springProfilesActive;

    @Value(("${spring.datasource.url:#{null}}")) // default is null
    private String springDatasourceUrl;

    @RequestMapping("/app-title")
    public String appTitle() {
        return "Config property 'appTitle' has value >>>" + appTitle + "<<<";
    }

    @RequestMapping("/spring-profiles-active")
    public String springProfilesActive() {
        return "Config property 'springProfilesActive' has value >>>" + springProfilesActive + "<<<";
    }

    @RequestMapping("/spring-datasource-url")
    public String springDatasourceUrl() {
        return "Config property 'springDatasourceUrl' has value >>>" + springDatasourceUrl + "<<<";
    }
}
