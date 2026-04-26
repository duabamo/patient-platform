package com.healthcare.reporting.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientSummaryDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String socialSecurityNumber;
    private LocalDate birthDate;
    private String gender;
    private String email;
    private String phoneNumber;
    private String referringDoctor;
    private List<PathologyInfoDTO> pathologies;
    private List<TreatmentInfoDTO> treatments;
}
