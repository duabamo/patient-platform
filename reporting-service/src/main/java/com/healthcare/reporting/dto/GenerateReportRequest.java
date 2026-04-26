package com.healthcare.reporting.dto;

import com.healthcare.reporting.enums.ReportType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateReportRequest {
    @NotNull
    private ReportType type;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private UUID patientId; // null = rapport global
}
