package com.example.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.usermanagement.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // <-- Bắt buộc phải có để DataInitializer chạy
    Optional<Role> findByName(String name);
}
@Transactional
public void setAdminRole(Long userId) {
    User user = userRepository.findById(userId).orElseThrow();
    Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
    
    // Thêm quyền admin vào danh sách quyền hiện tại
    user.getRoles().add(adminRole);
    userRepository.save(user);
    // Sau bước này, user đó login lại sẽ vào được trang admin
}