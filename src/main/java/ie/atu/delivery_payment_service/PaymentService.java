package ie.atu.delivery_payment_service;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Payment processPayment(Payment payment) {

        String paymentStatus = mockPaymentProcessing();
        payment.setPaymentId(UUID.randomUUID().toString()); // Generate a random payment ID
        payment.setStatus(paymentStatus);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);
        notifyPaymentStatus(savedPayment);
        return savedPayment;
    }

    private String mockPaymentProcessing() {
        return ThreadLocalRandom.current().nextInt(100) < 80 ? "Successful" : "Failed";
    }

    private void notifyPaymentStatus(Payment payment) {
        Map<String, String> notification = new HashMap<>();
        notification.put("orderId", payment.getOrderId());
        notification.put("paymentId", payment.getPaymentId());
        notification.put("status", payment.getStatus());
        rabbitTemplate.convertAndSend("payment-status-queue", notification);

        System.out.println("Payment notification sent: " + notification);
    }
}


