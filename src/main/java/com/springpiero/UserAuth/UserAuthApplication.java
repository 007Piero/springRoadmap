package com.springpiero.UserAuth;

import com.springpiero.UserAuth.configs.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import io.swagger.v3.oas.annotations.enums.*;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
@SecurityScheme(name = "app-secure-scheme", scheme = "bearer", type = SecuritySchemeType.HTTP)
public class UserAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserAuthApplication.class, args);
	}

}
