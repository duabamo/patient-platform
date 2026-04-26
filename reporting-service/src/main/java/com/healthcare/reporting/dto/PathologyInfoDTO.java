package com.healthcare.reporting.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PathologyInfoDTO {
    private UUID id;
    private String type;
    private String name;
    private String severity;
    private String icd10Code;
    private LocalDate diagnosisDate;
    private boolean active;
}
