package com.healthcare.reporting.repository;

import com.healthcare.reporting.entity.Report;
import com.healthcare.reporting.enums.ReportStatus;
import com.healthcare.reporting.enums.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    Page<Report> findByType(ReportType type, Pageable pageable);

    List<Report> findByPatientId(UUID patientId);

    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    List<Report> findByTypeAndPeriodStartAndPeriodEnd(ReportType type, LocalDate start, LocalDate end);
}
