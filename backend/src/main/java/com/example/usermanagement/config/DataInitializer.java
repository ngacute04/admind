package com.example.usermanagement.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.UserRepository;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ===== 1. TẠO ROLE NẾU CHƯA TỒN TẠI =====
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_ADMIN");
                    return roleRepository.save(role);
                });

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_USER");
                    return roleRepository.save(role);
                });

        // ===== 2. TẠO TÀI KHOẢN ADMIN MẶC ĐỊNH =====
        String adminEmail = "nga@gmail.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User adminUser = new User();
            // Bổ sung username để tránh lỗi NOT NULL constraint
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode("120204"));
            adminUser.setEnabled(true);
            adminUser.setRoles(Set.of(adminRole));

            userRepository.save(adminUser);
            System.out.println("--> Created Admin User");
        }

        // ===== 3. TẠO TÀI KHOẢN USER MẶC ĐỊNH =====
        String userEmail = "user@example.com";
        if (userRepository.findByEmail(userEmail).isEmpty()) {
            User normalUser = new User();
            // Bổ sung username để tránh lỗi NOT NULL constraint
            normalUser.setEmail(userEmail);
            normalUser.setPassword(passwordEncoder.encode("password"));
            normalUser.setEnabled(true);
            normalUser.setRoles(Set.of(userRole));

            userRepository.save(normalUser);
            System.out.println("--> Created Default User");
        }

        System.out.println("=== DataInitializer: Roles and default users are set up successfully ===");
    }
}