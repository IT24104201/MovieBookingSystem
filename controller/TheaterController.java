package com.movieticket.controller;

import com.movieticket.model.Theater;
import com.movieticket.service.TheaterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
public class TheaterController {

    private final TheaterService theaterService;

    public TheaterController(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    @PostMapping
    public ResponseEntity<Theater> create(@RequestBody Theater theater) {
        return ResponseEntity.ok(theaterService.create(theater));
    }

    @GetMapping
    public ResponseEntity<List<Theater>> getAll() {
        return ResponseEntity.ok(theaterService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Theater> getById(@PathVariable int id) {
        return theaterService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Theater> update(@PathVariable int id, @RequestBody Theater updated) {
        Theater theater = theaterService.update(id, updated);
        return theater != null ? ResponseEntity.ok(theater) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        theaterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
