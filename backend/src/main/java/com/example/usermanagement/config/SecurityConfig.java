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

    // 1. Password Encoder dùng BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Cấu hình bảo mật
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 2.1 CSRF (nếu bạn dùng form login Thymeleaf, nên giữ enabled; nếu Ajax API mới disable)
            .csrf(csrf -> csrf.disable()) // Nếu muốn production, hãy enable và thêm token

            // 2.2 Phân quyền truy cập
            .authorizeHttpRequests(auth -> auth
                // Cho phép truy cập công khai: login, forgot-password, static resources
                .requestMatchers("/login", "/forgot-password", "/css/**", "/js/**", "/images/**").permitAll()
                
                // Chỉ ADMIN mới truy cập được /admin/**
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Tất cả URL còn lại phải đăng nhập
                .anyRequest().authenticated()
            )

            // 2.3 Cấu hình Form Login custom
            .formLogin(form -> form
                .loginPage("/login")                   // Trang login custom
                .usernameParameter("email")            // Lấy từ input name="email"
                .passwordParameter("password")         // Lấy từ input name="password"
                .defaultSuccessUrl("/admin", true)     // Redirect sau khi login thành công
                .failureUrl("/login?error=true")       // Redirect khi login fail
                .permitAll()                            // Cho phép tất cả truy cập login page
            )

            // 2.4 Cấu hình Logout
            .logout(logout -> logout
                .logoutUrl("/logout")                   // Endpoint logout
                .logoutSuccessUrl("/login?logout")     // Redirect sau khi logout
                .invalidateHttpSession(true)           // Hủy session
                .deleteCookies("JSESSIONID")           // Xóa cookie session
                .permitAll()
            )

            // 2.5 Remember-me (tuỳ chọn, nếu muốn nhớ login lâu)
            // .rememberMe(r -> r
            //     .key("uniqueAndSecret")
            //     .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 ngày
            // )

            ;

        return http.build();
    }
}
