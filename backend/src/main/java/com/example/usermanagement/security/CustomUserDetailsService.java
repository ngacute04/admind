package com.example.usermanagement.security;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String loginInput) throws UsernameNotFoundException {

        // logic: Tìm bằng Email trước, nếu không thấy thì tìm bằng Số điện thoại
        User user = userRepository.findByEmail(loginInput)
                .orElseGet(() -> userRepository.findByPhone(loginInput)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại: " + loginInput)));

        // Gom tất cả Role và Permission thành Authorities
        Set<GrantedAuthority> authorities = new HashSet<>();

        user.getRoles().forEach(role -> {
            // 1. Thêm Role (VD: ROLE_USER)
            String roleName = role.getName().toUpperCase();
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }
            authorities.add(new SimpleGrantedAuthority(roleName));

            // 2. Thêm các quyền Permission cụ thể (VD: READ, WRITE)
            if (role.getPermissions() != null) {
                role.getPermissions().forEach(p -> {
                    authorities.add(new SimpleGrantedAuthority(p.getName().toUpperCase()));
                });
            }
        });

        return new org.springframework.security.core.userdetails.User(
                loginInput, // Dùng chính đầu vào (Email/SĐT) để Spring định danh phiên làm việc
                user.getPassword(),
                user.isEnabled(), 
                true, true, true,
                authorities
        );
    }
}