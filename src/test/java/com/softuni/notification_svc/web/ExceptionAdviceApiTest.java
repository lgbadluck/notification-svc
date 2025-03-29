package com.softuni.notification_svc.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.softuni.notification_svc.web.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
public class ExceptionAdviceApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postNoStaticResource_shouldErrorResponseAndStatusNotFound() throws Exception {

        // 1. Build Request
        MockHttpServletRequestBuilder request = post("/api/v1/notifications/not-404-found");

        // Serialize BudgetRequest to JSON
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not supported application endpoint.");
        String expectedJson = objectMapper.writeValueAsString(errorResponse);

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

}
