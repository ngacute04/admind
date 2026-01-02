package com.example.usermanagement.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.usermanagement.repository.UserRepository;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // Chỉ cho phép tài khoản có quyền ADMIN truy cập
public class AdminController {

    private final UserRepository userRepository;

    // Sử dụng Constructor Injection thay vì @Autowired (Khuyến nghị của Spring)
    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Trang Dashboard tổng quan
     */
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        // Lấy số liệu thực tế từ Database
        long totalUsers = userRepository.count();
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalPosts", 1240); // Giả lập dữ liệu bài viết
        model.addAttribute("activeUsers", 85);   // Giả lập dữ liệu online
        
        model.addAttribute("pageTitle", "RedZone - Quản trị hệ thống");
        
        // Dòng này truyền mảnh nội dung Dashboard vào Layout chính
        model.addAttribute("pageContent", "admin/dashboard :: content");
        
        return "index"; // Trả về file index.html làm layout chính
    }

    /**
     * Quản lý danh sách người dùng
     */
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("pageTitle", "Quản lý thành viên");
        
        // Truyền mảnh nội dung Users vào Layout
        model.addAttribute("pageContent", "admin/users :: content");
        
        return "index";
    }

    /**
     * Quản lý nội dung bài viết
     */
    @GetMapping("/contents")
    public String contents(Model model) {
        model.addAttribute("pageTitle", "Kiểm duyệt nội dung");
        model.addAttribute("pageContent", "admin/contents :: content");
        return "index";
    }

    /**
     * Cài đặt hệ thống
     */
    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("pageTitle", "Cấu hình hệ thống");
        
        // Truyền mảnh nội dung Settings vào Layout
        model.addAttribute("pageContent", "admin/settings :: content");
        
        return "index";
    }

    /**
     * Quản lý báo cáo/khiếu nại
     */
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("pageTitle", "Báo cáo vi phạm");
        model.addAttribute("pageContent", "admin/reports :: content");
        return "index";
    }
}