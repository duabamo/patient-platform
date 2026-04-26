package com.healthcare.reporting;

import com.healthcare.reporting.client.PatientProfileClient;
import com.healthcare.reporting.dto.GenerateReportRequest;
import com.healthcare.reporting.dto.PageResponse;
import com.healthcare.reporting.dto.PatientSummaryDTO;
import com.healthcare.reporting.dto.ReportDTO;
import com.healthcare.reporting.entity.Report;
import com.healthcare.reporting.enums.ReportStatus;
import com.healthcare.reporting.enums.ReportType;
import com.healthcare.reporting.repository.ReportRepository;
import com.healthcare.reporting.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private ReportRepository reportRepository;
    @Mock private PatientProfileClient patientProfileClient;
    @InjectMocks private ReportService reportService;

    @Test
    void generateGlobalWeeklyReport_shouldComputeAggregates() {
        when(patientProfileClient.listPatients(anyInt(), anyInt()))
                .thenReturn(PageResponse.<PatientSummaryDTO>builder()
                        .content(Collections.emptyList())
                        .number(0).size(100).totalElements(0).totalPages(0).last(true)
                        .build());
        when(reportRepository.save(any(Report.class))).thenAnswer(inv -> {
            Report r = inv.getArgument(0);
            if (r.getId() == null) r.setId(UUID.randomUUID());
            return r;
        });

        ReportDTO dto = reportService.generate(GenerateReportRequest.builder()
                .type(ReportType.WEEKLY)
                .build());

        assertThat(dto).isNotNull();
        assertThat(dto.getStatus()).isEqualTo(ReportStatus.COMPLETED);
        assertThat(dto.getData()).containsKeys("scope", "totalPatients", "pathologyDistribution");
    }
}
