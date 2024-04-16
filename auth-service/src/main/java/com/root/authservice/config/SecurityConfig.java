package com.root.authservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final FilterChainConfig filterChainConfig;

    public SecurityConfig(FilterChainConfig filterChainConfig) {
        this.filterChainConfig = filterChainConfig;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests(req -> {
                    //req.antMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll();
                    req.antMatchers(HttpMethod.DELETE, "/api/v1/employee/disable/**").hasAnyRole("ADMIN", "HUMAN_RESOURCES");
                    req.antMatchers(HttpMethod.POST, "/api/v1/employee/attach-superior").hasAnyRole("ADMIN", "HUMAN_RESOURCES", "MANAGER");
                    req.antMatchers(HttpMethod.POST, "/api/v1/employee/register").hasAnyRole("ADMIN", "HUMAN_RESOURCES");
                    req.antMatchers(HttpMethod.POST, "/api/v1/employee/register/manager").hasAnyRole("ADMIN", "HUMAN_RESOURCES");
                    req.antMatchers(HttpMethod.POST, "/api/v1/employee/register/hr").hasRole("ADMIN");
                    req.antMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll();
                    req.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filterChainConfig, UsernamePasswordAuthenticationFilter.class)
                .cors().and().csrf().disable()
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
