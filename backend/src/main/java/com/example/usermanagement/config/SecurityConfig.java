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
            .csrf(csrf -> csrf.disable()) // Vô hiệu hóa CSRF để thuận tiện cho gọi API từ React/Postman
            .authorizeHttpRequests(auth -> auth
                // 1. Tài nguyên tĩnh và xác thực không cần login
                .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                
                // 2. Mở cổng WebSocket cho Chat (rất quan trọng cho file WebSocketConfig bạn đã tạo)
                .requestMatchers("/ws-redzone/**").permitAll()
                
                // 3. Phân quyền khu vực Admin (Bắt buộc phải có ROLE_ADMIN)
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // 4. Mọi request khác đều phải đăng nhập
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    // Logic điều hướng thông minh dựa trên Role
                    var authorities = authentication.getAuthorities();
                    boolean isAdmin = authorities.stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    
                    if (isAdmin) {
                        response.sendRedirect("/admin/dashboard");
                    } else {
                        response.sendRedirect("/home"); // Redirect về trang mạng xã hội
                    }
                })
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                // Nếu User thường cố tình vào /admin, đá về trang home kèm cảnh báo
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/home?denied=true");
                })
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}