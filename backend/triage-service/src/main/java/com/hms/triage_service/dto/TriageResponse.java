package com.hms.triage_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriageResponse {

    /** Suggested department, e.g. "Cardiology". */
    private String department;

    /** 1 (routine) .. 5 (life-threatening emergency). */
    private int urgency;

    /** Human label for the urgency, e.g. "CRITICAL". */
    private String urgencyLabel;

    /** What the patient should do next. */
    private String recommendedAction;

    /** Short explanation of the classification. */
    private String reason;
}
