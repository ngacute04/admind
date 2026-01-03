package com.example.usermanagement.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.usermanagement.repository.UserRepository;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // Tường lửa vòng 1: Chỉ ADMIN mới vào được toàn bộ Class này
public class AdminController {

    private final UserRepository userRepository;

    // Constructor Injection
    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Trang Dashboard tổng quan
     */
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalPosts", 1240); 
        model.addAttribute("activeUsers", 85);   
        
        model.addAttribute("pageTitle", "RedZone - Quản trị hệ thống");
        model.addAttribute("pageContent", "admin/dashboard :: content");
        
        return "index"; 
    }

    /**
     * Quản lý danh sách người dùng
     */
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("pageTitle", "Quản lý thành viên");
        model.addAttribute("pageContent", "admin/users :: content");
        return "index";
    }

    /**
     * Xóa người dùng - Kiểm tra quyền hạn chuyên sâu
     */
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_PERMISSION')") // Tường lửa vòng 2: Phải có quyền XÓA cụ thể
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users?success=deleted";
    }

    @GetMapping("/contents")
    public String contents(Model model) {
        model.addAttribute("pageTitle", "Kiểm duyệt nội dung");
        model.addAttribute("pageContent", "admin/contents :: content");
        return "index";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("pageTitle", "Báo cáo vi phạm");
        model.addAttribute("pageContent", "admin/reports :: content");
        return "index";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("pageTitle", "Cấu hình hệ thống");
        model.addAttribute("pageContent", "admin/settings :: content");
        return "index";
    }
}