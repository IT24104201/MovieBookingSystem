package com.movieticket.controller;

import com.movieticket.model.Movie;
import com.movieticket.model.Showtime;
import com.movieticket.model.Theater;
import com.movieticket.model.User;
import com.movieticket.service.MovieService;
import com.movieticket.service.ShowtimeService;
import com.movieticket.service.TheaterService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/api/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;
    private final MovieService movieService;
    private final TheaterService theaterService;

    public ShowtimeController(ShowtimeService showtimeService,
                              MovieService movieService,
                              TheaterService theaterService) {
        this.showtimeService = showtimeService;
        this.movieService = movieService;
        this.theaterService = theaterService;
    }

    // ---------- JSON CRUD ----------
    @PostMapping
    @ResponseBody
    public ResponseEntity<Showtime> create(@RequestBody Showtime showtime) {
        return ResponseEntity.ok(showtimeService.create(showtime));
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Showtime>> getAll() {
        return ResponseEntity.ok(showtimeService.getAll());
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Showtime> getById(@PathVariable int id) {
        return showtimeService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Showtime> update(@PathVariable int id, @RequestBody Showtime updated) {
        Showtime showtime = showtimeService.update(id, updated);
        return showtime != null ? ResponseEntity.ok(showtime) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable int id) {
        showtimeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Admin form endpoints ----------
    @PostMapping("/admin/create")
    public String adminCreateShowtime(
            HttpSession session,
            @RequestParam int movieId,
            @RequestParam int theaterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate showDate,
            @RequestParam("startTime") @DateTimeFormat(pattern = "HH:mm") LocalTime startTime
    ) {
        if (!hasAnyRole(session, "THEATER_SHOWTIME_ADMIN")) return "redirect:/api/users/login";

        Movie movie = movieService.getById(movieId).orElse(null);
        Theater theater = theaterService.getById(theaterId).orElse(null);
        if (movie == null || theater == null) return "redirect:/api/users/admin/dashboard#showtimes";

        Showtime s = new Showtime();
        s.setMovie(movie);
        s.setTheater(theater);
        s.setShowDate(showDate);
        s.setShowTime(startTime);
        showtimeService.create(s);
        return "redirect:/api/users/admin/dashboard#showtimes";
    }

    @PostMapping("/admin/update/{id}")
    public String adminUpdateShowtime(
            HttpSession session,
            @PathVariable int id,
            @RequestParam(required = false) Integer movieId,
            @RequestParam(required = false) Integer theaterId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate showDate,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime startTime
    ) {
        if (!hasAnyRole(session, "THEATER_SHOWTIME_ADMIN")) return "redirect:/api/users/login";

        return showtimeService.getById(id).map(existing -> {
            if (movieId != null)   movieService.getById(movieId).ifPresent(existing::setMovie);
            if (theaterId != null) theaterService.getById(theaterId).ifPresent(existing::setTheater);
            if (showDate != null)  existing.setShowDate(showDate);
            if (startTime != null) existing.setShowTime(startTime);
            showtimeService.create(existing); // save
            return "redirect:/api/users/admin/dashboard#showtimes";
        }).orElse("redirect:/api/users/admin/dashboard#showtimes");
    }

    @PostMapping("/admin/delete/{id}")
    public String adminDeleteShowtime(HttpSession session, @PathVariable int id) {
        if (!hasAnyRole(session, "THEATER_SHOWTIME_ADMIN")) return "redirect:/api/users/login";
        showtimeService.delete(id);
        return "redirect:/api/users/admin/dashboard#showtimes";
    }

    // SUPER_ADMIN always allowed
    private boolean hasAnyRole(HttpSession session, String... roles) {
        Object obj = session.getAttribute("loggedUser");
        if (!(obj instanceof User u)) return false;
        if ("SUPER_ADMIN".equals(u.getRole())) return true;
        for (String r : roles) if (r.equals(u.getRole())) return true;
        return false;
    }
}
