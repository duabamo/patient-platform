package com.healthcare.patientprofile.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pathologies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pathology {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PathologyType type;

    @Column(length = 50)
    private String icd10Code; // Code CIM-10

    @Column(length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Severity severity;

    @Column(nullable = false)
    private LocalDate diagnosisDate;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference
    private Patient patient;

    public enum PathologyType {
        DIABETES_TYPE_1,
        DIABETES_TYPE_2,
        HYPERTENSION,
        HEART_FAILURE,
        CHRONIC_KIDNEY_DISEASE,
        COPD,
        ASTHMA,
        OTHER
    }

    public enum Severity { MILD, MODERATE, SEVERE, CRITICAL }
}
