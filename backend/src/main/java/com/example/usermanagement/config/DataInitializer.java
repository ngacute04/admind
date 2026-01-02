package com.example.usermanagement.config;

import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Khởi tạo Roles
        Role adminRole = initRole("ROLE_ADMIN");
        Role userRole = initRole("ROLE_USER");

        // 2. Khởi tạo tài khoản Admin tối cao (Người cấp quyền cho người khác)
        initUser("nga@gmail.com", "120204", "Administrator", adminRole);

        // 3. Khởi tạo tài khoản User mẫu (Để test luồng mạng xã hội)
        initUser("user@example.com", "password", "Default User", userRole);

        System.out.println("=== DataInitializer: Ready for RedZone Social Network ===");
    }

    private Role initRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return roleRepository.save(role);
                });
    }

    private void initUser(String email, String password, String fullName, Role role) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setFullName(fullName);
            user.setEnabled(true);
            user.setRoles(Set.of(role));
            userRepository.save(user);
            System.out.println("--> Created account: " + email + " with role: " + role.getName());
        }
    }
}