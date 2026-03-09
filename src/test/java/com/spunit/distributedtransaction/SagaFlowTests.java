package com.spunit.distributedtransaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SagaFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCompleteSagaWhenNoStepFails() throws Exception {
        mockMvc.perform(post("/api/sagas/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId":"ORD-SAGA-100",
                                  "customerId":"CUST-100",
                                  "productCode":"PRD-1",
                                  "quantity":2,
                                  "amount":120.00,
                                  "failOnInventory":false,
                                  "failOnPayment":false,
                                  "failOnShipping":false
                                }
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    void shouldCompensateSagaWhenPaymentFails() throws Exception {
        mockMvc.perform(post("/api/sagas/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId":"ORD-SAGA-101",
                                  "customerId":"CUST-101",
                                  "productCode":"PRD-2",
                                  "quantity":1,
                                  "amount":80.00,
                                  "failOnInventory":false,
                                  "failOnPayment":true,
                                  "failOnShipping":false
                                }
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.status").value("COMPENSATED"))
                .andExpect(jsonPath("$.data.failureReason").value("Payment charge failed"));
    }
}

