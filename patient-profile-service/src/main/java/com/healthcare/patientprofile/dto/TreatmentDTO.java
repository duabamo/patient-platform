package com.healthcare.patientprofile.dto;

import com.healthcare.patientprofile.entity.Treatment.AdministrationRoute;
import jakarta.validation.constraints.NotBlank;
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
public class TreatmentDTO {

    private UUID id;

    @NotBlank(message = "Le nom du medicament est requis")
    @Size(max = 200)
    private String medicationName;

    @Size(max = 100)
    private String activeIngredient;

    @NotBlank(message = "La posologie est requise")
    @Size(max = 100)
    private String dosage;

    @NotBlank(message = "La frequence est requise")
    @Size(max = 100)
    private String frequency;

    private AdministrationRoute route;

    @NotNull(message = "La date de debut est requise")
    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 100)
    private String prescribingDoctor;

    @Size(max = 1000)
    private String instructions;

    private boolean active;

    private UUID patientId;
}
