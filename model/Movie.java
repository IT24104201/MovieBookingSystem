package com.movieticket.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;



/**
 * Movie entity: stores film details available for booking.
 */
@Entity
@Table(name = "Movies")
@Data
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private int movieId;

    private String title;
    private String genre;

    @Column(name = "duration_minutes")
    private Integer durationMinutes; // in minutes

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "poster_url") // was posterUrl without column annotation
    private String posterUrl;

    private String description;

    private String rating;

    @Column(name = "trailer_url")
    private String trailerUrl;
}
