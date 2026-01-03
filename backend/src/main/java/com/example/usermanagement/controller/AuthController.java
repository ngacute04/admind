package com.example.usermanagement.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashSet;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.UserRepository;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login"; 
    }
    // THÊM ĐOẠN NÀY VÀO AUTHCONTROLLER
@GetMapping("/register")
public String showRegisterPage() {
    return "auth/register"; // Trả về file templates/auth/register.html
}

    @PostMapping("/register")
public String handleRegister(
        @RequestParam String fullName,
        @RequestParam int dob_day,
        @RequestParam int dob_month,
        @RequestParam int dob_year,
        @RequestParam String gender,
        @RequestParam String email,
        @RequestParam String password,
        org.springframework.ui.Model model // ⬅️ THÊM
) {
    try {
        // --- BƯỚC 1: KIỂM TRA TUỔI ---
        LocalDate birthDate = LocalDate.of(dob_year, dob_month, dob_day);
        LocalDate now = LocalDate.now();
        int age = Period.between(birthDate, now).getYears();

        if (age < 16) {
            model.addAttribute("errorMessage",
                    "Yêu cầu hệ thống: Bạn phải đủ 16 tuổi để đăng ký.");
            return "auth/register";
        }

        // --- BƯỚC 2: KIỂM TRA EMAIL / SĐT ---
        boolean isValidEmail = email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        boolean isValidPhone = email.matches("^0[35789][0-9]{8}$");

        if (!isValidEmail && !isValidPhone) {
            model.addAttribute("errorMessage",
                    "Định dạng Email hoặc Số điện thoại không hợp lệ.");
            return "auth/register";
        }

        if (password.length() <= 6
                || !password.matches(".*[a-zA-Z].*")
                || !password.matches(".*[0-9].*")) {
            model.addAttribute("errorMessage",
                    "Mật khẩu phải trên 6 ký tự, bao gồm cả chữ và số.");
            return "auth/register";
        }

        // --- BƯỚC 3: KIỂM TRA TRÙNG ---
        if ((isValidEmail && userRepository.findByEmail(email).isPresent())
                || (isValidPhone && userRepository.findByPhone(email).isPresent())) {
            model.addAttribute("errorMessage",
                    "Tài khoản này đã tồn tại trên RedZone.");
            return "auth/register";
        }

        // --- BƯỚC 4: TẠO USER ---
        String birthdayStr = String.format("%02d/%02d/%d", dob_day, dob_month, dob_year);

        User.UserBuilder userBuilder = User.builder()
                .fullName(fullName)
                .password(passwordEncoder.encode(password))
                .gender(gender)
                .birthday(birthdayStr)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .roles(new HashSet<>());

        if (isValidPhone) {
            userBuilder.phone(email);
            userBuilder.email("phone_" + email + "@redzone.com");
        } else {
            userBuilder.email(email);
        }

        User user = userBuilder.build();

        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER chưa được khởi tạo!"));

        user.addRole(roleUser);
        userRepository.save(user);

        // ✅ THÀNH CÔNG → Ở LẠI REGISTER
        model.addAttribute("successMessage",
                "Đăng ký thành công! Hệ thống sẽ chuyển bạn sang trang đăng nhập.");

        return "auth/register";

    } catch (Exception e) {
        model.addAttribute("errorMessage",
                "Ngày sinh không hợp lệ hoặc lỗi hệ thống.");
        return "auth/register";
    }
}
}