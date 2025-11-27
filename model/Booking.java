package com.movieticket.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Booking entity: customer selects movie/showtime/seat.
 */
@Entity
@Table(name = "Bookings")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private int bookingId;

    @Column(name = "user_id")
    private int userId; // FK to Users

    @Column(name = "showtime_id")
    private int showtimeId; // FK to Showtimes (if implemented)

    @Column(name = "seat_number")
    private String seatNumber;

    private String status; // PENDING / CONFIRMED / CANCELLED
}
