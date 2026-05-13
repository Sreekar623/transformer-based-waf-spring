package com.iomp.WAF.config;

import com.iomp.WAF.Filter.WafFilter;
import com.iomp.WAF.Repository.RequestLogRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final RequestLogRepository repository;

    public SecurityConfig(RequestLogRepository repository) {
        this.repository = repository;
    }

    @Bean
    public WafFilter wafFilter() {
        return new WafFilter(repository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(wafFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}