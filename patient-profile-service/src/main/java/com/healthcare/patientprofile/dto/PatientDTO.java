package com.healthcare.patientprofile.dto;

import com.healthcare.patientprofile.entity.Patient.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDTO {

    private UUID id;

    @NotBlank(message = "Le prenom est requis")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Le nom est requis")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "Le numero de securite sociale est requis")
    @Size(max = 50)
    private String socialSecurityNumber;

    @NotNull(message = "La date de naissance est requise")
    @Past(message = "La date de naissance doit etre dans le passe")
    private LocalDate birthDate;

    @NotNull(message = "Le genre est requis")
    private Gender gender;

    private String address;

    @Pattern(regexp = "^[+]?[0-9\\s-]{8,20}$", message = "Numero de telephone invalide")
    private String phoneNumber;

    @Email(message = "Email invalide")
    private String email;

    private String emergencyContactName;
    private String emergencyContactPhone;
    private String referringDoctor;

    private List<PathologyDTO> pathologies;
    private List<TreatmentDTO> treatments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
