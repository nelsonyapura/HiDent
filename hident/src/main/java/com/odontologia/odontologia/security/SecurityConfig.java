package com.odontologia.odontologia.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

        private final JwtAuthenticationFilter jwtAuthFilter;

        public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthFilter) {
                this.jwtAuthFilter = jwtAuthFilter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/auth/**", "/css/**", "/js/**", "/img/**",
                                                                "/favicon.ico")
                                                .permitAll()
                                                .anyRequest().authenticated())

                                .exceptionHandling(ex -> ex

                                                .defaultAuthenticationEntryPointFor(
                                                                (request, response, authException) -> {
                                                                        response.setStatus(401);
                                                                        response.setContentType(
                                                                                        "application/json;charset=UTF-8");
                                                                        response.getWriter().write(
                                                                                        "{\"error\":\"No autenticado\"}");
                                                                },
                                                                request -> request.getServletPath().startsWith("/api/"))

                                                .defaultAuthenticationEntryPointFor(
                                                                (request, response, authException) -> {
                                                                        response.sendRedirect("/auth/login");
                                                                },
                                                                request -> !request.getServletPath()
                                                                                .startsWith("/api/")))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/auth/login?logout")
                                                .deleteCookies("JWT")
                                                .invalidateHttpSession(true))
                                .build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
                        throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
