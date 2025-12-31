package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {


    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("pageContent", "admin/dashboard :: content");
        return "index";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("pageContent", "admin/users :: content");
        return "index";
    }

    @GetMapping("/contents")
    public String contents(Model model) {
        model.addAttribute("pageContent", "admin/contents :: content");
        return "index";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("pageContent", "admin/reports :: content");
        return "index";
    }

    @GetMapping("/marketplace")
    public String marketplace(Model model) {
        model.addAttribute("pageContent", "admin/marketplace :: content");
        return "index";
    }

    @GetMapping("/ads")
    public String ads(Model model) {
        model.addAttribute("pageContent", "admin/ads :: content");
        return "index";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("pageContent", "admin/settings :: content");
        return "index";
    }
}