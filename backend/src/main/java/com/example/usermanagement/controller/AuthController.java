package com.example.usermanagement.controller;

import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

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

    // Trang Login - TRUYỀN QUA LAYOUT
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("pageContent", "auth/login :: content");
        return "login-layout"; // Trả về file có chứa CSS/Particles
    }

    // Trang Register - TRUYỀN QUA LAYOUT
    @GetMapping("/register")
    public String showRegisterForm(@RequestParam(value = "error", required = false) String error,
                                   @RequestParam(value = "success", required = false) String success,
                                   Model model) {
        if ("email_exists".equals(error)) {
            model.addAttribute("errorMessage", "Email này đã được sử dụng!");
        }
        if (success != null) {
            model.addAttribute("successMessage", "Đăng ký thành công!");
        }
        
        // Quan trọng: Truyền fragment đăng ký vào layout
        model.addAttribute("pageContent", "auth/register :: content");
        return "login-layout"; 
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String fullName,
                                 @RequestParam(required = false) String phone) {

        if (userRepository.findByEmail(email).isPresent()) {
            return "redirect:/register?error=email_exists";
        }

        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER chưa có!"));

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setEnabled(true);
        user.setRoles(Set.of(roleUser));

        userRepository.save(user);

        return "redirect:/login?register_success";
    }

    @GetMapping("/home")
    public String home() {
        return "home"; 
    }
}