package com.example.usermanagement.controller;

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

/*************  ✨ Windsurf Command ⭐  *************/
    /**
     * Display the users page.
     * This page will show the list of all users registered in the system.
     * The page will also have a link to create a new user.
     */
/*******  29b3d5bc-ee5e-4a73-9412-83f0e43f4698  *******/
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