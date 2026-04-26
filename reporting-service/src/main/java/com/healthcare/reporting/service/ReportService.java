package com.healthcare.reporting.service;

import com.healthcare.reporting.client.PatientProfileClient;
import com.healthcare.reporting.dto.*;
import com.healthcare.reporting.entity.Report;
import com.healthcare.reporting.enums.ReportStatus;
import com.healthcare.reporting.enums.ReportType;
import com.healthcare.reporting.exception.ResourceNotFoundException;
import com.healthcare.reporting.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final PatientProfileClient patientProfileClient;

    public ReportDTO generate(GenerateReportRequest request) {
        LocalDate[] period = resolvePeriod(request);
        LocalDate periodStart = period[0];
        LocalDate periodEnd = period[1];

        Report report = Report.builder()
                .type(request.getType())
                .status(ReportStatus.GENERATING)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .patientId(request.getPatientId())
                .title(buildTitle(request.getType(), periodStart, periodEnd, request.getPatientId()))
                .build();
        report = reportRepository.save(report);

        try {
            Map<String, Object> data = (request.getPatientId() != null)
                    ? buildPatientReport(request.getPatientId(), periodStart, periodEnd)
                    : buildGlobalReport(periodStart, periodEnd);
            report.setData(data);
            report.setStatus(ReportStatus.COMPLETED);
            report.setCompletedAt(LocalDateTime.now());
        } catch (Exception ex) {
            log.error("Echec generation rapport {}", report.getId(), ex);
            report.setStatus(ReportStatus.FAILED);
            report.setErrorMessage(ex.getMessage());
        }

        return toDto(reportRepository.save(report));
    }

    private LocalDate[] resolvePeriod(GenerateReportRequest request) {
        LocalDate end = request.getPeriodEnd() != null ? request.getPeriodEnd() : LocalDate.now();
        LocalDate start = request.getPeriodStart();
        if (start == null) {
            start = switch (request.getType()) {
                case WEEKLY -> end.minusWeeks(1);
                case MONTHLY -> end.minusMonths(1);
                case CUSTOM -> end.minusWeeks(1);
            };
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("periodStart doit etre avant ou egal a periodEnd");
        }
        return new LocalDate[]{start, end};
    }

    private String buildTitle(ReportType type, LocalDate start, LocalDate end, UUID patientId) {
        String scope = patientId != null ? "Patient " + patientId : "Global";
        return String.format("Rapport %s - %s - du %s au %s", type, scope, start, end);
    }

    private Map<String, Object> buildPatientReport(UUID patientId, LocalDate start, LocalDate end) {
        PatientSummaryDTO patient = patientProfileClient.getPatient(patientId);
        if (patient == null) {
            throw new ResourceNotFoundException("Patient", patientId);
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("scope", "PATIENT");
        data.put("patient", buildPatientSection(patient));
        data.put("period", Map.of("start", start, "end", end, "days", ChronoUnit.DAYS.between(start, end)));
        data.put("pathologies", patient.getPathologies() != null ? patient.getPathologies() : Collections.emptyList());
        data.put("treatments", patient.getTreatments() != null ? patient.getTreatments() : Collections.emptyList());
        data.put("metrics", buildPatientMetrics(patient));
        return data;
    }

    private Map<String, Object> buildGlobalReport(LocalDate start, LocalDate end) {
        log.info("Generation rapport global du {} au {}", start, end);
        PageResponse<PatientSummaryDTO> page = patientProfileClient.listPatients(0, 1000);
        List<PatientSummaryDTO> patients = page.getContent() != null ? page.getContent() : Collections.emptyList();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("scope", "GLOBAL");
        data.put("period", Map.of("start", start, "end", end, "days", ChronoUnit.DAYS.between(start, end)));
        data.put("totalPatients", patients.size());
        data.put("pathologyDistribution", computePathologyDistribution(patients));
        data.put("genderDistribution", computeGenderDistribution(patients));
        data.put("ageDistribution", computeAgeDistribution(patients));
        data.put("activeTreatments", countActiveTreatments(patients));
        data.put("topMedications", computeTopMedications(patients, 10));

        return data;
    }

    private Map<String, Object> buildPatientSection(PatientSummaryDTO p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("name", p.getFirstName() + " " + p.getLastName());
        m.put("ssn", p.getSocialSecurityNumber());
        m.put("birthDate", p.getBirthDate());
        m.put("age", p.getBirthDate() != null ? Period.between(p.getBirthDate(), LocalDate.now()).getYears() : null);
        m.put("gender", p.getGender());
        m.put("referringDoctor", p.getReferringDoctor());
        return m;
    }

    private Map<String, Object> buildPatientMetrics(PatientSummaryDTO p) {
        long activePathologies = p.getPathologies() == null ? 0
                : p.getPathologies().stream().filter(PathologyInfoDTO::isActive).count();
        long activeTreatments = p.getTreatments() == null ? 0
                : p.getTreatments().stream().filter(TreatmentInfoDTO::isActive).count();
        return Map.of(
                "activePathologiesCount", activePathologies,
                "activeTreatmentsCount", activeTreatments,
                "totalPathologies", p.getPathologies() == null ? 0 : p.getPathologies().size(),
                "totalTreatments", p.getTreatments() == null ? 0 : p.getTreatments().size()
        );
    }

    private Map<String, Long> computePathologyDistribution(List<PatientSummaryDTO> patients) {
        Map<String, Long> dist = new HashMap<>();
        for (PatientSummaryDTO p : patients) {
            if (p.getPathologies() == null) continue;
            for (PathologyInfoDTO path : p.getPathologies()) {
                if (path.getType() == null || !path.isActive()) continue;
                dist.merge(path.getType(), 1L, Long::sum);
            }
        }
        return dist;
    }

    private Map<String, Long> computeGenderDistribution(List<PatientSummaryDTO> patients) {
        return patients.stream()
                .filter(p -> p.getGender() != null)
                .collect(Collectors.groupingBy(PatientSummaryDTO::getGender, Collectors.counting()));
    }

    private Map<String, Long> computeAgeDistribution(List<PatientSummaryDTO> patients) {
        Map<String, Long> dist = new LinkedHashMap<>();
        dist.put("0-17", 0L);
        dist.put("18-34", 0L);
        dist.put("35-49", 0L);
        dist.put("50-64", 0L);
        dist.put("65+", 0L);
        LocalDate today = LocalDate.now();
        for (PatientSummaryDTO p : patients) {
            if (p.getBirthDate() == null) continue;
            int age = Period.between(p.getBirthDate(), today).getYears();
            String bucket = age < 18 ? "0-17" : age < 35 ? "18-34" : age < 50 ? "35-49" : age < 65 ? "50-64" : "65+";
            dist.merge(bucket, 1L, Long::sum);
        }
        return dist;
    }

    private long countActiveTreatments(List<PatientSummaryDTO> patients) {
        return patients.stream()
                .filter(p -> p.getTreatments() != null)
                .flatMap(p -> p.getTreatments().stream())
                .filter(TreatmentInfoDTO::isActive)
                .count();
    }

    private List<Map<String, Object>> computeTopMedications(List<PatientSummaryDTO> patients, int topN) {
        Map<String, Long> counts = new HashMap<>();
        for (PatientSummaryDTO p : patients) {
            if (p.getTreatments() == null) continue;
            for (TreatmentInfoDTO t : p.getTreatments()) {
                if (!t.isActive() || t.getMedicationName() == null) continue;
                counts.merge(t.getMedicationName(), 1L, Long::sum);
            }
        }
        return counts.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(topN)
                .map(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("medication", e.getKey());
                    item.put("count", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReportDTO findById(UUID id) {
        return toDto(reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report", id)));
    }

    @Transactional(readOnly = true)
    public Page<ReportDTO> findAll(Pageable pageable) {
        return reportRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ReportDTO> findByType(ReportType type, Pageable pageable) {
        return reportRepository.findByType(type, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<ReportDTO> findByPatient(UUID patientId) {
        return reportRepository.findByPatientId(patientId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public void delete(UUID id) {
        if (!reportRepository.existsById(id)) throw new ResourceNotFoundException("Report", id);
        reportRepository.deleteById(id);
    }

    private ReportDTO toDto(Report r) {
        return ReportDTO.builder()
                .id(r.getId())
                .type(r.getType())
                .status(r.getStatus())
                .periodStart(r.getPeriodStart())
                .periodEnd(r.getPeriodEnd())
                .patientId(r.getPatientId())
                .title(r.getTitle())
                .data(r.getData())
                .errorMessage(r.getErrorMessage())
                .createdAt(r.getCreatedAt())
                .completedAt(r.getCompletedAt())
                .build();
    }
}
