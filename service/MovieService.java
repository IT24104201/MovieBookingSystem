package com.movieticket.service;

import com.movieticket.model.Movie;
import com.movieticket.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie create(Movie movie) {
        return movieRepository.save(movie);
    }

    public List<Movie> getAll() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getById(int id) {
        return movieRepository.findById(id);
    }

    public Movie update(int id, Movie updated) {
        return movieRepository.findById(id).map(movie -> {
            movie.setTitle(updated.getTitle());
            movie.setGenre(updated.getGenre());
            movie.setDurationMinutes(updated.getDurationMinutes());
            movie.setReleaseDate(updated.getReleaseDate());
            movie.setPosterUrl(updated.getPosterUrl());
            movie.setDescription(updated.getDescription());
            return movieRepository.save(movie);
        }).orElse(null);
    }


    public void delete(int id) {
        movieRepository.deleteById(id);
    }
}
