package com.movieticket.service;

import com.movieticket.model.Showtime;
import com.movieticket.repository.ShowtimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository) {
        this.showtimeRepository = showtimeRepository;
    }

    public Showtime create(Showtime showtime) {
        return showtimeRepository.save(showtime);
    }

    public List<Showtime> getAll() {
        return showtimeRepository.findAll();
    }

    public Optional<Showtime> getById(int id) {
        return showtimeRepository.findById(id);
    }

    @Transactional
    public Showtime update(int id, Showtime updated) {
        return showtimeRepository.findById(id).map(showtime -> {
            if (updated.getMovie() != null)   showtime.setMovie(updated.getMovie());
            if (updated.getTheater() != null) showtime.setTheater(updated.getTheater());
            if (updated.getShowDate() != null) showtime.setShowDate(updated.getShowDate());
            if (updated.getShowTime() != null) showtime.setShowTime(updated.getShowTime());
            return showtime; // dirty checking persists changes
        }).orElse(null);
    }

    public void delete(int id) {
        showtimeRepository.deleteById(id);
    }
}
