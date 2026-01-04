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
            @RequestParam String contact,        // Đổi tên từ email → contact cho rõ nghĩa
            @RequestParam String password,
            RedirectAttributes redirectAttributes
    ) {
        try {
            /* ---------- 1. KIỂM TRA NGÀY SINH & TUỔI ---------- */
            LocalDate birthDate = LocalDate.of(dob_year, dob_month, dob_day);
            if (birthDate.isAfter(LocalDate.now().minusYears(16))) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Yêu cầu hệ thống: Bạn phải đủ 16 tuổi để đăng ký.");
                return "redirect:/register";
            }

            /* ---------- 2. KIỂM TRA ĐỊNH DẠNG CONTACT ---------- */
            boolean isEmail = contact.matches("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
            boolean isPhone = contact.matches("^0[1-9][0-9]{8}$"); // Hỗ trợ tất cả đầu số VN hợp lệ

            if (!isEmail && !isPhone) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Vui lòng nhập Email hợp lệ hoặc Số điện thoại Việt Nam (10 số).");
                return "redirect:/register";
            }

            /* ---------- 3. KIỂM TRA MẬT KHẨU ---------- */
            if (password.length() < 6 ||
                !password.matches(".*[a-zA-Z].*") ||
                !password.matches(".*[0-9].*")) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Mật khẩu phải từ 6 ký tự trở lên, chứa cả chữ cái và số.");
                return "redirect:/register";
            }

            /* ---------- 4. XÁC ĐỊNH EMAIL THỰC & KIỂM TRA TRÙNG ---------- */
            String finalEmail;
            if (isPhone) {
                finalEmail = "phone_" + contact + "@redzone.local"; // Dùng domain nội bộ để tránh conflict thật
            } else {
                finalEmail = contact;
            }

            // Kiểm tra trùng cả email thật lẫn email giả từ SĐT
            if (userRepository.findByEmail(finalEmail).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        isPhone ? "Số điện thoại này đã được sử dụng." : "Email này đã được sử dụng.");
                return "redirect:/register";
            }

            // Nếu là SĐT, kiểm tra thêm trường phone để tránh trùng SĐT từ user khác
            if (isPhone && userRepository.findByPhone(contact).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Số điện thoại này đã được sử dụng.");
                return "redirect:/register";
            }

            /* ---------- 5. TẠO USER ---------- */
            User user = User.builder()
                    .fullName(fullName.trim())
                    .password(passwordEncoder.encode(password))
                    .gender(gender)
                    .birthday(String.format("%02d/%02d/%d", dob_day, dob_month, dob_year))
                    .email(finalEmail)
                    .phone(isPhone ? contact : null)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .roles(new HashSet<>())
                    .build();

            Role roleUser = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: ROLE_USER not found in database."));

            user.addRole(roleUser);
            userRepository.save(user);

            /* ---------- 6. THÀNH CÔNG ---------- */
            redirectAttributes.addFlashAttribute("successMessage",
                    "Chúc mừng! Tài khoản đã được tạo thành công. Bạn có thể đăng nhập ngay bây giờ.");

            return "redirect:/login"; // ← QUAN TRỌNG: Chuyển sang trang login

        } catch (Exception e) {
            // Bao gồm cả DateTimeException nếu ngày không hợp lệ (30/2, etc.)
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ngày sinh không hợp lệ hoặc có lỗi hệ thống. Vui lòng thử lại.");
            return "redirect:/register";
        }
    }
}