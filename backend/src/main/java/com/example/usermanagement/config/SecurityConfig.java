package com.example.usermanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 1. Tắt CSRF để tránh bị chặn khi dùng Form Login tùy chỉnh (nếu chưa xử lý Token ở Frontend)
            .csrf(csrf -> csrf.disable())
            
            // 2. Cấu hình phân quyền
            .authorizeHttpRequests(auth -> auth
                // Cho phép truy cập công khai trang login và các tài nguyên static (css, js)
                .requestMatchers("/login", "/forgot-password", "/css/**", "/js/**").permitAll()
                // Chỉ ADMIN mới vào được /admin/**
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Tất cả các request khác phải đăng nhập
                .anyRequest().authenticated()
            )
            
            // 3. Cấu hình Form Login
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email") // SPRING SẼ LẤY GIÁ TRỊ TỪ Ô NHẬP CÓ name="email"
                .defaultSuccessUrl("/admin", true) // Chuyển hướng đến /admin sau khi login
                .failureUrl("/login?error=true")
                .permitAll()
            )
            
            // 4. Cấu hình Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            );

        return http.build();
    }
}