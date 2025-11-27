package com.movieticket.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Payment entity: linked to booking.
 */
@Entity
@Table(name = "Payments")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private int paymentId;

    @Column(name = "booking_id")
    private int bookingId; // FK to Bookings

    private Double amount;

    // ✅ Map correctly to the existing DB column "method"
    @Column(name = "method", nullable = false)
    private String paymentMethod; // e.g., Card, Cash

    private String status; // PENDING / SUCCESS / FAILED
}
