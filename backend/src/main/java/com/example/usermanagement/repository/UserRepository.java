package com.example.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.usermanagement.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Tìm kiếm bằng Email cho Login
    Optional<User> findByEmail(String email);
    
    // Tìm kiếm bằng Số điện thoại cho Login
    Optional<User> findByPhone(String phone); 
}