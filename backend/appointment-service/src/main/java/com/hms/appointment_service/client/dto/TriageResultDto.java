package com.hms.appointment_service.client.dto;

import lombok.Data;

@Data
public class TriageResultDto {
    private String department;
    private int urgency;
    private String urgencyLabel;
    private String recommendedAction;
    private String reason;
}
