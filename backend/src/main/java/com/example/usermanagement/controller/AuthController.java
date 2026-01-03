package com.example.usermanagement.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    /* ===================== LOGIN ===================== */

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    /* ===================== REGISTER ===================== */

    @GetMapping("/register")
    public String showRegisterPage() {
        return "auth/register";
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
            RedirectAttributes redirectAttributes
    ) {

        try {
            /* ---------- 1. KIỂM TRA TUỔI (CHUẨN TUYỆT ĐỐI) ---------- */
            LocalDate birthDate = LocalDate.of(dob_year, dob_month, dob_day);
            LocalDate eligibleDate = birthDate.plusYears(16);

            if (LocalDate.now().isBefore(eligibleDate)) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Yêu cầu hệ thống: Bạn phải đủ 16 tuổi để đăng ký."
                );
                return "redirect:/register";
            }

            /* ---------- 2. KIỂM TRA EMAIL / SĐT ---------- */
            boolean isValidEmail = email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$");
            boolean isValidPhone = email.matches("^0[35789][0-9]{8}$");

            if (!isValidEmail && !isValidPhone) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Định dạng Email hoặc Số điện thoại không hợp lệ."
                );
                return "redirect:/register";
            }

            /* ---------- 3. KIỂM TRA MẬT KHẨU ---------- */
            if (password.length() < 6
                    || !password.matches(".*[a-zA-Z].*")
                    || !password.matches(".*[0-9].*")) {

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Mật khẩu phải từ 6 ký tự trở lên và bao gồm chữ + số."
                );
                return "redirect:/register";
            }

            /* ---------- 4. KIỂM TRA TRÙNG TÀI KHOẢN ---------- */
            if (isValidEmail && userRepository.findByEmail(email).isPresent()) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Email này đã được sử dụng."
                );
                return "redirect:/register";
            }

            if (isValidPhone && userRepository.findByPhone(email).isPresent()) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Số điện thoại này đã được sử dụng."
                );
                return "redirect:/register";
            }

            /* ---------- 5. TẠO USER ---------- */
            String birthdayStr = String.format("%02d/%02d/%d",
                    dob_day, dob_month, dob_year);

            User user = User.builder()
                    .fullName(fullName)
                    .password(passwordEncoder.encode(password))
                    .gender(gender)
                    .birthday(birthdayStr)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .roles(new HashSet<>())
                    .build();

            if (isValidPhone) {
                user.setPhone(email);
                user.setEmail("phone_" + email + "@redzone.com");
            } else {
                user.setEmail(email);
            }

            Role roleUser = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() ->
                            new RuntimeException("ROLE_USER chưa được khởi tạo"));

            user.addRole(roleUser);
            userRepository.save(user);

            /* ---------- 6. THÀNH CÔNG ---------- */
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đăng ký thành công! Hệ thống sẽ chuyển bạn sang trang đăng nhập."
            );

            return "redirect:/register";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Ngày sinh không hợp lệ hoặc lỗi hệ thống."
            );
            return "redirect:/register";
        }
    }
}
