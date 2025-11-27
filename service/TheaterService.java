package com.movieticket.service;

import com.movieticket.model.Theater;
import com.movieticket.repository.TheaterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TheaterService {

    private final TheaterRepository theaterRepository;

    public TheaterService(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    public Theater create(Theater theater) {
        return theaterRepository.save(theater);
    }

    public List<Theater> getAll() {
        return theaterRepository.findAll();
    }

    public Optional<Theater> getById(int id) {
        return theaterRepository.findById(id);
    }

    @Transactional
    public Theater update(int id, Theater updated) {
        return theaterRepository.findById(id).map(theater -> {
            theater.setName(updated.getName());
            theater.setLocation(updated.getLocation());
            theater.setTotalScreens(updated.getTotalScreens()); // ✅ camelCase
            return theater; // dirty-checking persists within @Transactional
        }).orElse(null);
    }

    public void delete(int id) {
        theaterRepository.deleteById(id);
    }
}
