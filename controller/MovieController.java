package com.movieticket.controller;

import com.movieticket.model.Feedback;
import com.movieticket.model.Movie;
import com.movieticket.model.User;
import com.movieticket.service.FeedbackService;
import com.movieticket.service.MovieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;
    private final FeedbackService feedbackService;

    public MovieController(MovieService movieService, FeedbackService feedbackService) {
        this.movieService = movieService;
        this.feedbackService = feedbackService;
    }

    // ---------- JSON CRUD ----------
    @PostMapping
    @ResponseBody
    public ResponseEntity<Movie> create(@RequestBody Movie movie) {
        return ResponseEntity.ok(movieService.create(movie));
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Movie>> getAll() {
        return ResponseEntity.ok(movieService.getAll());
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Movie> getById(@PathVariable int id) {
        return movieService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Movie> update(@PathVariable int id, @RequestBody Movie updated) {
        Movie movie = movieService.update(id, updated);
        return movie != null ? ResponseEntity.ok(movie) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable int id) {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Customer pages ----------
    @GetMapping("/home")
    public String customerHome(Model model) {
        model.addAttribute("movies", movieService.getAll());
        return "home";
    }

    @GetMapping("/details/{id}")
    public String movieDetails(@PathVariable int id, Model model) {
        Optional<Movie> movie = movieService.getById(id);
        if (movie.isEmpty()) return "redirect:/api/movies/home";
        model.addAttribute("movie", movie.get());
        model.addAttribute("feedbackList", feedbackService.getByMovieId(id));
        return "movie-details";
    }

    // ---------- Admin form endpoints ----------
    @PostMapping("/admin/create")
    public String adminCreateMovie(
            HttpSession session,
            @RequestParam String title,
            @RequestParam String genre,
            @RequestParam(required = false) Integer durationMinutes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDate,
            @RequestParam(required = false) String posterUrl,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String rating,
            @RequestParam(required = false) String trailerUrl
    ) {
        if (!hasAnyRole(session, "MOVIE_ADMIN")) return "redirect:/api/users/login";
        Movie m = new Movie();
        m.setTitle(title);
        m.setGenre(genre);
        if (durationMinutes != null) m.setDurationMinutes(durationMinutes);
        if (releaseDate != null)   m.setReleaseDate(releaseDate);
        m.setPosterUrl(posterUrl);
        m.setDescription(description);
        m.setRating(rating);
        m.setTrailerUrl(trailerUrl);
        movieService.create(m);
        return "redirect:/api/users/admin/dashboard#movies";
    }

    @PostMapping("/admin/update/{id}")
    public String adminUpdateMovie(
            HttpSession session,
            @PathVariable int id,
            @RequestParam String title,
            @RequestParam String genre,
            @RequestParam(required = false) Integer durationMinutes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDate,
            @RequestParam(required = false) String posterUrl,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String rating,
            @RequestParam(required = false) String trailerUrl
    ) {
        if (!hasAnyRole(session, "MOVIE_ADMIN")) return "redirect:/api/users/login";
        Movie m = new Movie();
        m.setTitle(title);
        m.setGenre(genre);
        if (durationMinutes != null) m.setDurationMinutes(durationMinutes);
        if (releaseDate != null)     m.setReleaseDate(releaseDate);
        m.setPosterUrl(posterUrl);
        m.setDescription(description);
        m.setRating(rating);
        m.setTrailerUrl(trailerUrl);
        movieService.update(id, m);
        return "redirect:/api/users/admin/dashboard#movies";
    }

    @PostMapping("/admin/delete/{id}")
    public String adminDeleteMovie(HttpSession session, @PathVariable int id) {
        if (!hasAnyRole(session, "MOVIE_ADMIN")) return "redirect:/api/users/login";
        movieService.delete(id);
        return "redirect:/api/users/admin/dashboard#movies";
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
