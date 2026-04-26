package com.healthcare.patientprofile.controller;

import com.healthcare.patientprofile.dto.PathologyDTO;
import com.healthcare.patientprofile.service.PathologyService;
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
@Tag(name = "Pathologies", description = "Gestion des pathologies des patients")
public class PathologyController {

    private final PathologyService pathologyService;

    @PostMapping("/patients/{patientId}/pathologies")
    @Operation(summary = "Ajouter une pathologie a un patient")
    public ResponseEntity<PathologyDTO> add(
            @PathVariable UUID patientId,
            @Valid @RequestBody PathologyDTO dto) {
        return ResponseEntity.ok(pathologyService.addToPatient(patientId, dto));
    }

    @GetMapping("/patients/{patientId}/pathologies")
    @Operation(summary = "Liste des pathologies d'un patient")
    public ResponseEntity<List<PathologyDTO>> listForPatient(
            @PathVariable UUID patientId,
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        return ResponseEntity.ok(activeOnly
                ? pathologyService.findActiveByPatient(patientId)
                : pathologyService.findByPatient(patientId));
    }

    @PutMapping("/pathologies/{id}")
    @Operation(summary = "Mettre a jour une pathologie")
    public ResponseEntity<PathologyDTO> update(@PathVariable UUID id, @RequestBody PathologyDTO dto) {
        return ResponseEntity.ok(pathologyService.update(id, dto));
    }

    @DeleteMapping("/pathologies/{id}")
    @Operation(summary = "Supprimer une pathologie")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        pathologyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
