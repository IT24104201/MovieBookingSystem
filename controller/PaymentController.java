package com.movieticket.controller;

import com.movieticket.model.Payment;
import com.movieticket.model.User;
import com.movieticket.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ---------- JSON CRUD ----------
    @PostMapping
    @ResponseBody
    public ResponseEntity<Payment> create(@RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.create(payment));
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Payment>> getAll() {
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Payment> getById(@PathVariable int id) {
        return paymentService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Payment> update(@PathVariable int id, @RequestBody Payment updated) {
        Payment payment = paymentService.update(id, updated);
        return payment != null ? ResponseEntity.ok(payment) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable int id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Customer page ----------
    @GetMapping("/new/{bookingId}")
    public String newPaymentPage(@PathVariable int bookingId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !"CUSTOMER".equals(user.getRole())) return "redirect:/api/users/login";
        Payment p = new Payment();
        p.setBookingId(bookingId);
        p.setStatus("PENDING");
        model.addAttribute("payment", p);
        return "payment-Page";
    }

    @PostMapping("/confirm")
    public String confirmPayment(@ModelAttribute Payment payment, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/api/users/login";
        payment.setStatus("SUCCESS");
        paymentService.create(payment);
        return "redirect:/api/movies/home";
    }

    // ---------- Admin form endpoints ----------
    @PostMapping("/admin/create")
    public String adminCreatePayment(HttpSession session,
                                     @RequestParam int bookingId,
                                     @RequestParam double amount,
                                     @RequestParam String status,
                                     @RequestParam(required = false) String paymentMethod) {
        if (!hasAnyRole(session, "PAYMENT_ADMIN")) return "redirect:/api/users/login";
        Payment p = new Payment();
        p.setBookingId(bookingId);
        p.setAmount(amount);
        p.setStatus(status);
        if (paymentMethod != null) p.setPaymentMethod(paymentMethod);
        paymentService.create(p);
        return "redirect:/api/users/admin/dashboard#payments";
    }

    @PostMapping("/admin/update/{id}")
    public String adminUpdatePayment(HttpSession session,
                                     @PathVariable int id,
                                     @RequestParam(required = false) Integer bookingId,
                                     @RequestParam(required = false) Double amount,
                                     @RequestParam String status,
                                     @RequestParam(required = false) String paymentMethod) {
        if (!hasAnyRole(session, "PAYMENT_ADMIN")) return "redirect:/api/users/login";
        Payment p = new Payment();
        if (bookingId != null) p.setBookingId(bookingId);
        if (amount != null) p.setAmount(amount);
        p.setStatus(status);
        if (paymentMethod != null) p.setPaymentMethod(paymentMethod);
        paymentService.update(id, p);
        return "redirect:/api/users/admin/dashboard#payments";
    }

    @PostMapping("/admin/delete/{id}")
    public String adminDeletePayment(HttpSession session, @PathVariable int id) {
        if (!hasAnyRole(session, "PAYMENT_ADMIN")) return "redirect:/api/users/login";
        paymentService.delete(id);
        return "redirect:/api/users/admin/dashboard#payments";
    }

    private boolean hasAnyRole(HttpSession session, String role) {
        Object obj = session.getAttribute("loggedUser");
        if (!(obj instanceof User u)) return false;
        return "SUPER_ADMIN".equals(u.getRole()) || role.equals(u.getRole());
    }
}
