package com.hms.triage_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.triage_service.client.GroqClient;
import com.hms.triage_service.dto.TriageRequest;
import com.hms.triage_service.dto.TriageResponse;
import com.hms.triage_service.exception.TriageException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TriageService {

    private final GroqClient groqClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** Departments the model is allowed to route to. */
    private static final List<String> DEPARTMENTS = List.of(
            "Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Dermatology",
            "Gastroenterology", "Pulmonology", "ENT", "Ophthalmology", "Gynecology",
            "Urology", "Psychiatry", "Dentistry", "Emergency", "General Medicine");

    private static final String SYSTEM_PROMPT = """
            You are a hospital triage assistant. Given a patient's symptoms, decide the most
            appropriate department and how urgent the case is. Respond with ONLY a JSON object,
            no prose, with exactly these keys:
              "department": one of %s
              "urgency": integer 1-5 where 1=routine, 2=low, 3=moderate, 4=urgent, 5=life-threatening emergency
              "recommendedAction": one short sentence telling the patient what to do
              "reason": one short sentence explaining the classification
            Route clear emergencies (chest pain with breathlessness, stroke signs, severe bleeding,
            difficulty breathing) to "Emergency" with urgency 5. If unsure, use "General Medicine".
            """.formatted(DEPARTMENTS);

    public TriageResponse triage(TriageRequest request) {
        String userPrompt = buildUserPrompt(request);
        String content = groqClient.complete(SYSTEM_PROMPT, userPrompt);

        JsonNode node;
        try {
            node = objectMapper.readTree(content);
        } catch (Exception e) {
            throw new TriageException("Could not parse triage model output: " + content, e);
        }

        int urgency = clampUrgency(node.path("urgency").asInt(3));
        String department = normalizeDepartment(node.path("department").asText("General Medicine"));

        return TriageResponse.builder()
                .department(department)
                .urgency(urgency)
                .urgencyLabel(label(urgency))
                .recommendedAction(node.path("recommendedAction").asText("Consult a doctor."))
                .reason(node.path("reason").asText(""))
                .build();
    }

    private String buildUserPrompt(TriageRequest request) {
        StringBuilder sb = new StringBuilder("Symptoms: ").append(request.getSymptoms());
        if (request.getAge() != null) {
            sb.append("\nAge: ").append(request.getAge());
        }
        if (request.getSex() != null && !request.getSex().isBlank()) {
            sb.append("\nSex: ").append(request.getSex());
        }
        return sb.toString();
    }

    private int clampUrgency(int urgency) {
        return Math.max(1, Math.min(5, urgency));
    }

    private String normalizeDepartment(String department) {
        return DEPARTMENTS.stream()
                .filter(d -> d.equalsIgnoreCase(department.trim()))
                .findFirst()
                .orElse("General Medicine");
    }

    private String label(int urgency) {
        return switch (urgency) {
            case 5 -> "CRITICAL";
            case 4 -> "URGENT";
            case 3 -> "MODERATE";
            case 2 -> "LOW";
            default -> "ROUTINE";
        };
    }
}
