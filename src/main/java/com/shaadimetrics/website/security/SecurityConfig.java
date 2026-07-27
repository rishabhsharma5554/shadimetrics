package com.shaadimetrics.website.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                .requestMatchers("/", "/gallery", "/offers", "/planner", "/consultation", "/thank-you").permitAll()
                .requestMatchers("/admin/login").permitAll()
                .requestMatchers("/admin/users/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/admin/leads/**").hasAnyRole("SUPER_ADMIN", "LEADS_MANAGER")
                .requestMatchers("/admin/testimonials/*/approve", "/admin/testimonials/*/reject")
                        .hasRole("SUPER_ADMIN")
                .requestMatchers("/admin/gallery/**", "/admin/testimonials/**",
                                  "/admin/services/**", "/admin/offers/**")
                        .hasAnyRole("SUPER_ADMIN", "CONTENT_MANAGER")
                .requestMatchers("/admin/**").authenticated()
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .formLogin((FormLoginConfigurer<HttpSecurity> form) -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .defaultSuccessUrl("/admin", true)
                .failureUrl("/admin/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex.accessDeniedPage("/admin/access-denied"));

        return http.build();
    }
}
