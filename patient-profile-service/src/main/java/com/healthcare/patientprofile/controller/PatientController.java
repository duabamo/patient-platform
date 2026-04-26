package com.healthcare.patientprofile.controller;

import com.healthcare.patientprofile.dto.PatientDTO;
import com.healthcare.patientprofile.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Gestion des donnees demographiques des patients")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @Operation(summary = "Liste paginee des patients")
    public ResponseEntity<Page<PatientDTO>> list(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(patientService.search(search, pageable));
        }
        return ResponseEntity.ok(patientService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer un patient par son ID")
    public ResponseEntity<PatientDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.findById(id));
    }

    @GetMapping("/by-pathology/{type}")
    @Operation(summary = "Liste des patients ayant une pathologie donnee")
    public ResponseEntity<List<PatientDTO>> findByPathology(@PathVariable String type) {
        return ResponseEntity.ok(patientService.findByPathologyType(type));
    }

    @PostMapping
    @Operation(summary = "Creer un nouveau patient")
    public ResponseEntity<PatientDTO> create(@Valid @RequestBody PatientDTO dto) {
        PatientDTO created = patientService.create(dto);
        return ResponseEntity
                .created(URI.create("/api/v1/patients/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre a jour un patient")
    public ResponseEntity<PatientDTO> update(@PathVariable UUID id, @Valid @RequestBody PatientDTO dto) {
        return ResponseEntity.ok(patientService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un patient")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/count")
    @Operation(summary = "Nombre total de patients")
    public ResponseEntity<Map<String, Long>> count() {
        return ResponseEntity.ok(Map.of("total", patientService.count()));
    }
}
