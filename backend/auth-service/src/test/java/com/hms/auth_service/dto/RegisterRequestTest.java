package com.hms.auth_service.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class RegisterRequestTest {

    @Test
    void shouldDeserializeRegisterRequestFromJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"name\":\"Jane\",\"email\":\"jane@example.com\",\"password\":\"secret\",\"role\":\"PATIENT\"}";

        RegisterRequest request = objectMapper.readValue(json, RegisterRequest.class);

        assertEquals("Jane", request.getName());
        assertEquals("jane@example.com", request.getEmail());
        assertEquals("secret", request.getPassword());
        assertEquals("PATIENT", request.getRole());
    }
}
