package com.healthcare.patientprofile.controller;

import com.healthcare.patientprofile.dto.TreatmentDTO;
import com.healthcare.patientprofile.service.TreatmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Treatments", description = "Gestion des traitements des patients")
public class TreatmentController {

    private final TreatmentService treatmentService;

    @PostMapping("/patients/{patientId}/treatments")
    @Operation(summary = "Ajouter un traitement a un patient")
    public ResponseEntity<TreatmentDTO> add(
            @PathVariable UUID patientId,
            @Valid @RequestBody TreatmentDTO dto) {
        return ResponseEntity.ok(treatmentService.addToPatient(patientId, dto));
    }

    @GetMapping("/patients/{patientId}/treatments")
    @Operation(summary = "Liste des traitements d'un patient")
    public ResponseEntity<List<TreatmentDTO>> listForPatient(
            @PathVariable UUID patientId,
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        return ResponseEntity.ok(activeOnly
                ? treatmentService.findActiveByPatient(patientId)
                : treatmentService.findByPatient(patientId));
    }

    @PutMapping("/treatments/{id}")
    @Operation(summary = "Mettre a jour un traitement")
    public ResponseEntity<TreatmentDTO> update(@PathVariable UUID id, @RequestBody TreatmentDTO dto) {
        return ResponseEntity.ok(treatmentService.update(id, dto));
    }

    @DeleteMapping("/treatments/{id}")
    @Operation(summary = "Supprimer un traitement")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        treatmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
