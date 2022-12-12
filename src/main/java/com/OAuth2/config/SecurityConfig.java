package com.OAuth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity // spring security filter 가 스프링 필터 체인에 등록된다.
public class SecurityConfig{

}
