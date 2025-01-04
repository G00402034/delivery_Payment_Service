package ie.atu.delivery_payment_service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessPayment() {

        Payment payment = new Payment();
        payment.setOrderId("12345");
        payment.setCustomerId("54321");
        payment.setAmount(100.50);
        payment.setCurrency("USD");
        payment.setPaymentMethod("Credit Card");

        Payment savedPayment = new Payment();
        savedPayment.setPaymentId(UUID.randomUUID().toString());
        savedPayment.setOrderId(payment.getOrderId());
        savedPayment.setCustomerId(payment.getCustomerId());
        savedPayment.setAmount(payment.getAmount());
        savedPayment.setCurrency(payment.getCurrency());
        savedPayment.setPaymentMethod(payment.getPaymentMethod());
        savedPayment.setStatus("Successful");
        savedPayment.setCreatedAt(LocalDateTime.now());
        savedPayment.setUpdatedAt(LocalDateTime.now());

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);


        Payment result = paymentService.processPayment(payment);


        assertNotNull(result);
        assertEquals("Successful", result.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("payment-status-queue"), any(Map.class));
    }
}

