package com.root.eurekagateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class EurekaGatewayApplication {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", route -> route.path("/auth-service/**")
                        .filters(filter -> filter.stripPrefix(1))
                        .uri("lb://AUTH-SERVICE"))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(EurekaGatewayApplication.class, args);
    }

}
