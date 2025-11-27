package com.movieticket.controller;

import com.movieticket.model.User;
import com.movieticket.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final MovieService movieService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final FeedbackService feedbackService;
    private final ShowtimeService showtimeService;

    private static final Set<String> ALLOWED_SIGNUP_ROLES =
            Set.of("CUSTOMER", "MOVIE_ADMIN", "BOOKING_ADMIN");

    public UserController(UserService userService,
                          MovieService movieService,
                          BookingService bookingService,
                          PaymentService paymentService,
                          FeedbackService feedbackService,
                          ShowtimeService showtimeService) {
        this.userService = userService;
        this.movieService = movieService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.feedbackService = feedbackService;
        this.showtimeService = showtimeService;
    }

    // ---------------- JSON CRUD ----------------
    @PostMapping
    @ResponseBody
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.create(user));
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id:\\d+}")
    @ResponseBody
    public ResponseEntity<User> getById(@PathVariable int id) {
        return userService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id:\\d+}")
    @ResponseBody
    public ResponseEntity<User> update(@PathVariable int id, @Valid @RequestBody User updated) {
        User user = userService.update(id, updated);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable int id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Authentication ----------------
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "registered", required = false) String registered,
                            Model model) {
        if (registered != null) {
            model.addAttribute("success", "Account created successfully! You can now sign in.");
        }
        return "login";
    }

    @PostMapping("/doLogin")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        return userService.login(email, password)
                .map(user -> {
                    session.setAttribute("loggedUser", user);
                    if ("CUSTOMER".equals(user.getRole())) return "redirect:/api/movies/home";
                    return "redirect:/api/users/admin/dashboard";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Invalid email or password");
                    return "login";
                });
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/api/users/login";
    }

    // ---------------- Registration (with role selection) ----------------
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", ALLOWED_SIGNUP_ROLES);
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user,
                           @RequestParam(name = "role", required = false) String roleFromForm,
                           Model model) {

        // Whitelist/normalize role; default to CUSTOMER
        String role = Optional.ofNullable(roleFromForm)
                .map(s -> s.toUpperCase(Locale.ROOT))
                .filter(ALLOWED_SIGNUP_ROLES::contains)
                .orElse("CUSTOMER");
        user.setRole(role);

        // Duplicate email check via service
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("roles", ALLOWED_SIGNUP_ROLES);
            model.addAttribute("error", "Email already in use.");
            return "register";
        }

        userService.create(user); // service should hash password
        return "redirect:/api/users/login?registered=1";
    }

    // ---------------- Admin Dashboard ----------------
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        User u = (User) session.getAttribute("loggedUser");
        if (u == null || !u.getRole().endsWith("ADMIN")) {
            return "redirect:/api/users/login";
        }

        String role = u.getRole();
        model.addAttribute("role", role);

        model.addAttribute("users", Collections.emptyList());
        model.addAttribute("movies", Collections.emptyList());
        model.addAttribute("showtimes", Collections.emptyList());
        model.addAttribute("bookings", Collections.emptyList());
        model.addAttribute("payments", Collections.emptyList());
        model.addAttribute("feedbackList", Collections.emptyList());

        switch (role) {
            case "SUPER_ADMIN" -> {
                model.addAttribute("users", userService.getAll());
                model.addAttribute("movies", movieService.getAll());
                model.addAttribute("showtimes", showtimeService.getAll());
                model.addAttribute("bookings", bookingService.getAll());
                model.addAttribute("payments", paymentService.getAll());
                model.addAttribute("feedbackList", feedbackService.getAll());
            }
            case "USER_ADMIN" -> model.addAttribute("users", userService.getAll());
            case "MOVIE_ADMIN" -> model.addAttribute("movies", movieService.getAll());
            case "THEATER_SHOWTIME_ADMIN" -> model.addAttribute("showtimes", showtimeService.getAll());
            case "BOOKING_ADMIN" -> model.addAttribute("bookings", bookingService.getAll());
            case "PAYMENT_ADMIN" -> model.addAttribute("payments", paymentService.getAll());
            case "FEEDBACK_ADMIN" -> model.addAttribute("feedbackList", feedbackService.getAll());
        }

        return "admin-dashboard";
    }

    // ---------------- Admin User CRUD ----------------
    @PostMapping("/admin/create")
    public String adminCreateUser(HttpSession session,
                                  @RequestParam String name,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String role) {
        if (!hasAnyRole(session, "USER_ADMIN")) return "redirect:/api/users/login";
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(password);
        u.setRole(role);
        userService.create(u);
        return "redirect:/api/users/admin/dashboard#users";
    }

    @PostMapping("/admin/update/{id:\\d+}")
    public String adminUpdateUser(HttpSession session,
                                  @PathVariable int id,
                                  @RequestParam String name,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String role) {
        if (!hasAnyRole(session, "USER_ADMIN")) return "redirect:/api/users/login";
        User updated = new User();
        updated.setName(name);
        updated.setEmail(email);
        updated.setPassword(password);
        updated.setRole(role);
        userService.update(id, updated);
        return "redirect:/api/users/admin/dashboard#users";
    }

    @PostMapping("/admin/delete/{id:\\d+}")
    public String adminDeleteUser(HttpSession session, @PathVariable int id) {
        if (!hasAnyRole(session, "USER_ADMIN")) return "redirect:/api/users/login";
        userService.delete(id);
        return "redirect:/api/users/admin/dashboard#users";
    }

    // SUPER_ADMIN always allowed
    private boolean hasAnyRole(HttpSession session, String... roles) {
        Object obj = session.getAttribute("loggedUser");
        if (!(obj instanceof User u)) return false;
        if ("SUPER_ADMIN".equals(u.getRole())) return true;
        for (String r : roles) if (r.equals(u.getRole())) return true;
        return false;
    }

    @GetMapping("/me")
    @ResponseBody
    public ResponseEntity<?> currentUser(HttpSession session) {
        User u = (User) session.getAttribute("loggedUser");
        if (u == null) return ResponseEntity.status(401).body(Map.of("authenticated", false));
        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "name", u.getName(),
                "email", u.getEmail(),
                "role", u.getRole()
        ));
    }
}
