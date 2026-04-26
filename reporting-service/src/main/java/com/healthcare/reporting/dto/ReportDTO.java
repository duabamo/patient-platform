package com.healthcare.reporting.dto;

import com.healthcare.reporting.enums.ReportStatus;
import com.healthcare.reporting.enums.ReportType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {
    private UUID id;
    private ReportType type;
    private ReportStatus status;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private UUID patientId;
    private String title;
    private Map<String, Object> data;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
