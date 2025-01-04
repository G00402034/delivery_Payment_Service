package ie.atu.delivery_payment_service;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testProcessPayment() throws Exception {

        Payment payment = new Payment();
        payment.setOrderId("12345");
        payment.setCustomerId("54321");
        payment.setAmount(100.50);
        payment.setCurrency("USD");
        payment.setPaymentMethod("Credit Card");

        Payment processedPayment = new Payment();
        processedPayment.setPaymentId(UUID.randomUUID().toString());
        processedPayment.setOrderId(payment.getOrderId());
        processedPayment.setCustomerId(payment.getCustomerId());
        processedPayment.setAmount(payment.getAmount());
        processedPayment.setCurrency(payment.getCurrency());
        processedPayment.setPaymentMethod(payment.getPaymentMethod());
        processedPayment.setStatus("Successful");
        processedPayment.setCreatedAt(LocalDateTime.now());
        processedPayment.setUpdatedAt(LocalDateTime.now());

        when(paymentService.processPayment(any(Payment.class))).thenReturn(processedPayment);


        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").exists())
                .andExpect(jsonPath("$.status").value("Successful"));
    }
}

