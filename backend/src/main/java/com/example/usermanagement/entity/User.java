package com.example.usermanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    private String phone;

    private String avatarUrl; // Link ảnh đại diện (Cloudinary)

    private boolean enabled = true; // Cho phép Admin khóa tài khoản nếu vi phạm

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // ----- Mối quan hệ Phân quyền -----
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // ----- Mối quan hệ Mạng xã hội (Bài viết) -----
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts = new HashSet<>();

    // Gán Role nhanh
    public void addRole(Role role) {
        this.roles.add(role);
    }
}