package com.healthcare.reporting.scheduler;

import com.healthcare.reporting.dto.GenerateReportRequest;
import com.healthcare.reporting.enums.ReportType;
import com.healthcare.reporting.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportScheduler {

    private final ReportService reportService;

    /**
     * Generation automatique du rapport hebdomadaire global
     * Tous les lundis a 02:00
     */
    @Scheduled(cron = "${reporting.weekly.cron:0 0 2 * * MON}")
    public void generateWeeklyGlobalReport() {
        log.info("Demarrage rapport hebdomadaire global");
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusWeeks(1);
        try {
            reportService.generate(GenerateReportRequest.builder()
                    .type(ReportType.WEEKLY)
                    .periodStart(start)
                    .periodEnd(end)
                    .build());
            log.info("Rapport hebdomadaire global genere avec succes");
        } catch (Exception ex) {
            log.error("Erreur lors de la generation du rapport hebdomadaire", ex);
        }
    }

    /**
     * Generation automatique du rapport mensuel global
     * Le 1er de chaque mois a 03:00
     */
    @Scheduled(cron = "${reporting.monthly.cron:0 0 3 1 * *}")
    public void generateMonthlyGlobalReport() {
        log.info("Demarrage rapport mensuel global");
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(1);
        try {
            reportService.generate(GenerateReportRequest.builder()
                    .type(ReportType.MONTHLY)
                    .periodStart(start)
                    .periodEnd(end)
                    .build());
            log.info("Rapport mensuel global genere avec succes");
        } catch (Exception ex) {
            log.error("Erreur lors de la generation du rapport mensuel", ex);
        }
    }
}
