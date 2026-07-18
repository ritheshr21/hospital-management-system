package com.hms.appointment_service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriageRequestDto {
    private String symptoms;
    private Integer age;
    private String sex;
}
