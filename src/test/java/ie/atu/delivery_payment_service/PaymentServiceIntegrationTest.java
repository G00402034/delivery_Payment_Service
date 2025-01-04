package ie.atu.delivery_payment_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testProcessPayment() {

        Payment payment = new Payment();
        payment.setOrderId("order123");
        payment.setCustomerId("customer123");
        payment.setAmount(50.00);
        payment.setCurrency("USD");
        payment.setPaymentMethod("Credit Card");


        ResponseEntity<Payment> response = restTemplate.postForEntity("/api/payments", payment, Payment.class);


        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("order123", response.getBody().getOrderId());
    }
}

