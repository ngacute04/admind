package com.example.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.usermanagement.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // <-- Bắt buộc phải có để DataInitializer chạy
    Optional<Role> findByName(String name);
}
