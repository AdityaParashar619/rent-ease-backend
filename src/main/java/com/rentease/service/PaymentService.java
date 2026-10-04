package com.rentease.service;

import com.rentease.entity.Booking;
import com.rentease.entity.Payment;
import com.rentease.enums.PaymentStatus;
import com.rentease.exception.ResourceNotFoundException;
import com.rentease.repository.BookingRepository;
import com.rentease.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService implements PaymentGateway {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public String initiateEscrowPayment(String bookingId, BigDecimal amount, String currency) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        Payment payment = new Payment();
        payment.setId("pay_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        payment.setBooking(booking);
        payment.setTransactionId("TXN-" + System.currentTimeMillis());
        payment.setPaymentMethod("UPI_ESCROW");
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setAmount(amount);
        payment.setGatewayReference("GATEWAY-SIM-AUTH");

        Payment saved = paymentRepository.save(payment);
        return saved.getTransactionId();
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        return signature != null && !signature.trim().isEmpty();
    }

    @Override
    @Transactional
    public boolean processRefund(String paymentId, BigDecimal refundAmount, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
        return true;
    }
}
