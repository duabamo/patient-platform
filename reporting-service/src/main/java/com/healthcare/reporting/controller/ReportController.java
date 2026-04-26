package com.healthcare.reporting.controller;

import com.healthcare.reporting.dto.GenerateReportRequest;
import com.healthcare.reporting.dto.ReportDTO;
import com.healthcare.reporting.enums.ReportType;
import com.healthcare.reporting.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Generation et consultation des rapports hebdomadaires et mensuels")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/generate")
    @Operation(summary = "Generer un rapport (hebdomadaire, mensuel ou personnalise)")
    public ResponseEntity<ReportDTO> generate(@Valid @RequestBody GenerateReportRequest request) {
        ReportDTO report = reportService.generate(request);
        return ResponseEntity.created(URI.create("/api/v1/reports/" + report.getId())).body(report);
    }

    @PostMapping("/weekly")
    @Operation(summary = "Generer un rapport hebdomadaire pour la derniere semaine")
    public ResponseEntity<ReportDTO> generateWeekly(@RequestParam(required = false) UUID patientId) {
        return ResponseEntity.ok(reportService.generate(GenerateReportRequest.builder()
                .type(ReportType.WEEKLY)
                .periodEnd(LocalDate.now())
                .periodStart(LocalDate.now().minusWeeks(1))
                .patientId(patientId)
                .build()));
    }

    @PostMapping("/monthly")
    @Operation(summary = "Generer un rapport mensuel pour le dernier mois")
    public ResponseEntity<ReportDTO> generateMonthly(@RequestParam(required = false) UUID patientId) {
        return ResponseEntity.ok(reportService.generate(GenerateReportRequest.builder()
                .type(ReportType.MONTHLY)
                .periodEnd(LocalDate.now())
                .periodStart(LocalDate.now().minusMonths(1))
                .patientId(patientId)
                .build()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer un rapport par ID")
    public ResponseEntity<ReportDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(reportService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Lister les rapports (pagine, filtre optionnel par type)")
    public ResponseEntity<Page<ReportDTO>> list(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) ReportType type) {
        if (type != null) return ResponseEntity.ok(reportService.findByType(type, pageable));
        return ResponseEntity.ok(reportService.findAll(pageable));
    }

    @GetMapping("/by-patient/{patientId}")
    @Operation(summary = "Lister les rapports d'un patient")
    public ResponseEntity<List<ReportDTO>> byPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(reportService.findByPatient(patientId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un rapport")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        reportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
