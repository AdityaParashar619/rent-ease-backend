package com.rentease.service;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;

@Service
public class PaymentService implements PaymentGateway {

    @Override
    public String initiateEscrowPayment(String bookingId, BigDecimal amount, String currency) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "A payment provider is not configured");
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        return false;
    }

    @Override
    public boolean processRefund(String paymentId, BigDecimal refundAmount, String reason) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "A payment provider is not configured");
    }
}
