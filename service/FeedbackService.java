package com.movieticket.service;

import com.movieticket.model.Feedback;
import com.movieticket.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    // Create (trim + clamp rating 1..5)
    public Feedback create(Feedback feedback) {
        if (feedback.getMessage() != null) {
            feedback.setMessage(feedback.getMessage().trim());
        }
        if (feedback.getRating() != null) {
            int r = Math.max(1, Math.min(5, feedback.getRating()));
            feedback.setRating(r);
        }
        return feedbackRepository.save(feedback);
    }

    // Read
    public List<Feedback> getAll() {
        return feedbackRepository.findAll();
    }

    public Optional<Feedback> getById(int id) {
        return feedbackRepository.findById(id);
    }

    public List<Feedback> getByMovieId(int movieId) {
        return feedbackRepository.findByMovieId(movieId);
    }

    /** Latest N feedback for a movie (fallback if repo has no "TopN" method) */
    public List<Feedback> getLatestByMovieId(int movieId, int limit) {
        return feedbackRepository.findByMovieId(movieId).stream()
                .sorted(Comparator.comparing(
                                        // if createdAt exists use it; otherwise keep stable order by id desc
                                        (Feedback f) -> f.getCreatedAt() != null ? f.getCreatedAt() : null,
                                        Comparator.nullsLast(Comparator.naturalOrder())
                                ).reversed()
                                .thenComparing(Feedback::getFeedbackId, Comparator.nullsLast(Comparator.reverseOrder()))
                )
                .limit(Math.max(1, limit))
                .collect(Collectors.toList());
    }

    // Partial Update
    @Transactional
    public Feedback update(int id, Feedback updated) {
        return feedbackRepository.findById(id).map(f -> {
            if (updated.getMessage() != null) {
                f.setMessage(updated.getMessage().trim());
            }
            if (updated.getRating() != null) {
                int r = Math.max(1, Math.min(5, updated.getRating()));
                f.setRating(r);
            }
            return f; // persisted via dirty checking
        }).orElse(null);
    }

    // Delete
    public void delete(int id) {
        feedbackRepository.deleteById(id);
    }
}
