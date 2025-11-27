package com.movieticket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    // Show login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // will load login.html
    }

    // Process login form
    @PostMapping("/doLogin")
    public String login(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               Model model) {

        // Temporary hardcoded check
        if (email.equals("admin@test.com") && password.equals("1234")) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }
}
