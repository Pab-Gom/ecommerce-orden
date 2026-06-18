package com.ecommerce.orden_service.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration

public class SecurityConfig{
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/ordenes/mis-ordenes").hasAnyRole("USUARIO","ADMIN")
                .requestMatchers(HttpMethod.GET, "/ordenes/{id}").hasAnyRole("USUARIO","ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/ordenes/{id}/estado").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/ordenes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/ordenes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/ordenes").hasAnyRole("USUARIO", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/ordenes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/ordenes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/ordenes/mis-ordenes").hasAnyRole("USUARIO","ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}