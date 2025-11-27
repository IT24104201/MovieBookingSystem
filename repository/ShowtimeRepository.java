package com.movieticket.repository;

import com.movieticket.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowtimeRepository extends JpaRepository<Showtime, Integer> {
}
