package com.movieticket.controller;

import com.movieticket.model.Booking;
import com.movieticket.model.User;
import com.movieticket.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // ---------- JSON CRUD ----------
    @PostMapping
    @ResponseBody
    public ResponseEntity<Booking> create(@RequestBody Booking booking) {
        return ResponseEntity.ok(bookingService.create(booking));
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Booking>> getAll() {
        return ResponseEntity.ok(bookingService.getAll());
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Booking> getById(@PathVariable int id) {
        return bookingService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Booking> update(@PathVariable int id, @RequestBody Booking updated) {
        Booking booking = bookingService.update(id, updated);
        return booking != null ? ResponseEntity.ok(booking) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable int id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Customer pages ----------
    @GetMapping("/new/{movieId}")
    public String newBookingPage(@PathVariable int movieId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !"CUSTOMER".equals(user.getRole())) return "redirect:/api/users/login";
        Booking booking = new Booking();
        booking.setUserId(user.getUserId());
        booking.setStatus("PENDING");
        model.addAttribute("movieId", movieId);
        model.addAttribute("booking", booking);
        return "booking-Page";
    }

    @PostMapping("/confirm")
    public String confirmBooking(@ModelAttribute Booking booking, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/api/users/login";
        booking.setUserId(user.getUserId());
        bookingService.create(booking);
        return "redirect:/api/payments/new/" + booking.getBookingId();
    }

    @GetMapping("/my")
    public String myBookings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !"CUSTOMER".equals(user.getRole())) return "redirect:/api/users/login";
        List<Booking> bookings = bookingService.getAll()
                .stream().filter(b -> b.getUserId() == user.getUserId()).toList();
        model.addAttribute("bookings", bookings);
        return "my-bookings";
    }

    // ---------- Admin form endpoints ----------
    @PostMapping("/admin/create")
    public String adminCreateBooking(HttpSession session,
                                     @RequestParam int userId,
                                     @RequestParam int showtimeId,
                                     @RequestParam String seatNumber,
                                     @RequestParam String status) {
        if (!hasAnyRole(session, "BOOKING_ADMIN")) return "redirect:/api/users/login";
        Booking b = new Booking();
        b.setUserId(userId);
        b.setShowtimeId(showtimeId);
        b.setSeatNumber(seatNumber);
        b.setStatus(status);
        bookingService.create(b);
        return "redirect:/api/users/admin/dashboard#bookings";
    }

    @PostMapping("/admin/update/{id}")
    public String adminUpdateBooking(HttpSession session,
                                     @PathVariable int id,
                                     @RequestParam(required = false) Integer userId,
                                     @RequestParam(required = false) Integer showtimeId,
                                     @RequestParam(required = false) String seatNumber,
                                     @RequestParam String status) {
        if (!hasAnyRole(session, "BOOKING_ADMIN")) return "redirect:/api/users/login";
        Booking b = new Booking();
        if (userId != null) b.setUserId(userId);
        if (showtimeId != null) b.setShowtimeId(showtimeId);
        if (seatNumber != null) b.setSeatNumber(seatNumber);
        b.setStatus(status);
        bookingService.update(id, b);
        return "redirect:/api/users/admin/dashboard#bookings";
    }

    @PostMapping("/admin/delete/{id}")
    public String adminDeleteBooking(HttpSession session, @PathVariable int id) {
        if (!hasAnyRole(session, "BOOKING_ADMIN")) return "redirect:/api/users/login";
        bookingService.delete(id);
        return "redirect:/api/users/admin/dashboard#bookings";
    }

    private boolean hasAnyRole(HttpSession session, String role) {
        Object obj = session.getAttribute("loggedUser");
        if (!(obj instanceof User u)) return false;
        return "SUPER_ADMIN".equals(u.getRole()) || role.equals(u.getRole());
    }
}
