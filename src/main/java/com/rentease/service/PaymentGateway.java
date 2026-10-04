package com.rentease.service;

import java.math.BigDecimal;

public interface PaymentGateway {
    String initiateEscrowPayment(String bookingId, BigDecimal amount, String currency);
    boolean verifyWebhookSignature(String payload, String signature);
    boolean processRefund(String paymentId, BigDecimal refundAmount, String reason);
}
