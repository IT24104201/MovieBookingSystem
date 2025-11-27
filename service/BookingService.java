package com.movieticket.service;

import com.movieticket.model.Booking;
import com.movieticket.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking create(Booking booking) {
        if (booking.getStatus() == null || booking.getStatus().isBlank()) {
            booking.setStatus("PENDING");
        }
        // If controller didn’t set these, they’ll be 0; that’s OK for create only if your DB allows it.
        // Add validations here if needed.
        return bookingRepository.save(booking);
    }

    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getById(int id) {
        return bookingRepository.findById(id);
    }

    @Transactional
    public Booking update(int id, Booking updated) {
        return bookingRepository.findById(id).map(b -> {
            // Because getters return primitive int, use sentinel checks
            if (updated.getShowtimeId() > 0) b.setShowtimeId(updated.getShowtimeId());
            if (updated.getUserId() > 0)     b.setUserId(updated.getUserId());

            if (updated.getSeatNumber() != null && !updated.getSeatNumber().isBlank()) {
                b.setSeatNumber(updated.getSeatNumber());
            }
            if (updated.getStatus() != null && !updated.getStatus().isBlank()) {
                b.setStatus(updated.getStatus());
            }
            // JPA dirty checking persists within @Transactional
            return b;
        }).orElse(null);
    }

    public void delete(int id) {
        bookingRepository.deleteById(id);
    }
}
