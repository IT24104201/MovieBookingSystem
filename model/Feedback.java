package com.movieticket.model;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

/**
 * Feedback entity: customers give rating + comment on movies.
 */
@Entity
@Table(name = "Feedback")
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Integer feedbackId;

    @Column(name = "user_id")
    private Integer userId; // FK to Users

    @Column(name = "movie_id")
    private Integer movieId; // FK to Movies

    private Integer rating; // 1-5

    private String message;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt; // ✅ Maps to DB column created_at
}
