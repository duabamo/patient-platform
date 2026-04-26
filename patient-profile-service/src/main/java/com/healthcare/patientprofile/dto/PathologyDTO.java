package com.healthcare.patientprofile.dto;

import com.healthcare.patientprofile.entity.Pathology.PathologyType;
import com.healthcare.patientprofile.entity.Pathology.Severity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PathologyDTO {

    private UUID id;

    @NotNull(message = "Le type de pathologie est requis")
    private PathologyType type;

    @Size(max = 50)
    private String icd10Code;

    @Size(max = 200)
    private String name;

    private Severity severity;

    @NotNull(message = "La date de diagnostic est requise")
    private LocalDate diagnosisDate;

    @Size(max = 1000)
    private String notes;

    private boolean active;

    private UUID patientId;
}
