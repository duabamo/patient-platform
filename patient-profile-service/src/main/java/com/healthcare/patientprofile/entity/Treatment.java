package com.healthcare.patientprofile.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "treatments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Treatment {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 200)
    private String medicationName;

    @Column(length = 100)
    private String activeIngredient;

    @Column(nullable = false, length = 100)
    private String dosage; // ex: "500mg"

    @Column(nullable = false, length = 100)
    private String frequency; // ex: "2 fois par jour"

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private AdministrationRoute route;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 100)
    private String prescribingDoctor;

    @Column(length = 1000)
    private String instructions;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference
    private Patient patient;

    public enum AdministrationRoute {
        ORAL, INJECTION, INHALATION, TOPICAL, SUBLINGUAL, OTHER
    }
}
