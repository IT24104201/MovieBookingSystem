package com.movieticket.model;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

/**
 * User entity for customers and admins.
 * Role decides permissions (CUSTOMER, MOVIE_ADMIN, BOOKING_ADMIN, etc).
 */
@Entity
@Table(name = "Users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")   // matches DB snake_case
    private int userId;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String role; // CUSTOMER / ADMIN type

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
}
