package com.movieticket.service;

import com.movieticket.model.Payment;
import com.movieticket.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Create
    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Read
    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getById(int id) {
        return paymentRepository.findById(id);
    }

    // Update
    public Payment update(int id, Payment updated) {
        return paymentRepository.findById(id).map(p -> {
            p.setBookingId(updated.getBookingId());
            p.setAmount(updated.getAmount());
            p.setStatus(updated.getStatus());
            p.setPaymentMethod(updated.getPaymentMethod());
            return paymentRepository.save(p);
        }).orElse(null);
    }

    // Delete
    public void delete(int id) {
        paymentRepository.deleteById(id);
    }
}
