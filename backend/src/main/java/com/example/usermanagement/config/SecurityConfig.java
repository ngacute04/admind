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
            .csrf(csrf -> csrf.disable()) // Tắt CSRF để làm Chat và API mượt hơn
            .authorizeHttpRequests(auth -> auth
                // 1. Công khai các trang Auth, CSS, JS, Image và WebSocket
                .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**", "/ws-redzone/**").permitAll()
                
                // 2. KHÓA CHẶT ADMIN: Chỉ ai có ROLE_ADMIN mới được đi qua cổng này
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // 3. Mọi trang còn lại phải Login mới xem được
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email") // Đây là input name="email" từ form login
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    // Logic điều hướng sau khi đăng nhập thành công
                    var authorities = authentication.getAuthorities();
                    boolean isAdmin = authorities.stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    
                    if (isAdmin) {
                        response.sendRedirect("/admin/dashboard");
                    } else {
                        // Người dùng thường sau khi đăng ký/đăng nhập sẽ luôn về /home
                        response.sendRedirect("/home");
                    }
                })
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                // Nếu User thường cố tình gõ /admin trên thanh địa chỉ -> Đá về /home
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/home?error=no_permission");
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