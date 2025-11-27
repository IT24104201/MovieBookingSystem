package com.movieticket.controller;

import com.movieticket.model.Feedback;
import com.movieticket.model.User;
import com.movieticket.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // ---------- JSON CRUD ----------
    @PostMapping
    @ResponseBody
    public ResponseEntity<Feedback> create(@RequestBody Feedback feedback) {
        return ResponseEntity.ok(feedbackService.create(feedback));
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Feedback>> getAll() {
        return ResponseEntity.ok(feedbackService.getAll());
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Feedback> getById(@PathVariable int id) {
        return feedbackService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/movie/{movieId}")
    @ResponseBody
    public ResponseEntity<List<Feedback>> getByMovie(@PathVariable int movieId) {
        return ResponseEntity.ok(feedbackService.getByMovieId(movieId));
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Feedback> update(@PathVariable int id, @RequestBody Feedback updated) {
        Feedback feedback = feedbackService.update(id, updated);
        return feedback != null ? ResponseEntity.ok(feedback) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable int id) {
        feedbackService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Customer form (Option B) ----------
    @PostMapping(value = "/customer/create", consumes = "application/x-www-form-urlencoded")
    public String customerCreateFeedback(
            HttpSession session,
            @RequestParam int movieId,
            @RequestParam int rating,
            @RequestParam String message
    ) {
        Object obj = session.getAttribute("loggedUser");
        if (!(obj instanceof User u)) return "redirect:/api/users/login";

        Feedback f = new Feedback();
        f.setUserId(u.getUserId()); // take user from session
        f.setMovieId(movieId);
        f.setRating(Math.max(1, Math.min(5, rating)));
        f.setMessage(message);

        feedbackService.create(f);
        // back to details page; you can add #reviews if you want to jump to list
        return "redirect:/api/movies/details/" + movieId;
    }

    // ---------- (Optional) Admin form endpoints you already had ----------
    @PostMapping("/admin/create")
    public String adminCreateFeedback(HttpSession session,
                                      @RequestParam int userId,
                                      @RequestParam int movieId,
                                      @RequestParam int rating,
                                      @RequestParam String message) {
        if (!hasAnyRole(session, "FEEDBACK_ADMIN")) return "redirect:/api/users/login";
        Feedback f = new Feedback();
        f.setUserId(userId);
        f.setMovieId(movieId);
        f.setRating(rating);
        f.setMessage(message);
        feedbackService.create(f);
        return "redirect:/api/users/admin/dashboard#feedback";
    }

    @PostMapping("/admin/update/{id}")
    public String adminUpdateFeedback(HttpSession session,
                                      @PathVariable int id,
                                      @RequestParam(required = false) Integer userId,
                                      @RequestParam(required = false) Integer movieId,
                                      @RequestParam(required = false) Integer rating,
                                      @RequestParam(required = false) String message) {
        if (!hasAnyRole(session, "FEEDBACK_ADMIN")) return "redirect:/api/users/login";
        Feedback f = new Feedback();
        if (userId != null) f.setUserId(userId);
        if (movieId != null) f.setMovieId(movieId);
        if (rating != null) f.setRating(rating);
        if (message != null) f.setMessage(message);
        feedbackService.update(id, f);
        return "redirect:/api/users/admin/dashboard#feedback";
    }

    @PostMapping("/admin/delete/{id}")
    public String adminDeleteFeedback(HttpSession session, @PathVariable int id) {
        if (!hasAnyRole(session, "FEEDBACK_ADMIN")) return "redirect:/api/users/login";
        feedbackService.delete(id);
        return "redirect:/api/users/admin/dashboard#feedback";
    }

    private boolean hasAnyRole(HttpSession session, String role) {
        Object obj = session.getAttribute("loggedUser");
        if (!(obj instanceof User u)) return false;
        return "SUPER_ADMIN".equals(u.getRole()) || role.equals(u.getRole());
    }
}
