package com.movie_back.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration // 配置类
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("影评项目 API")
						.description("所有 API 接口")
						.version("v1.0.0")
						.license(new License().name("Apache 2.0").url("http://springdoc.org")));
	}
}