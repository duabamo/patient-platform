package com.healthcare.reporting.client;

import com.healthcare.reporting.dto.PageResponse;
import com.healthcare.reporting.dto.PatientSummaryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

@Component
@Slf4j
public class PatientProfileClientFallback implements PatientProfileClient {

    @Override
    public PageResponse<PatientSummaryDTO> listPatients(int page, int size) {
        log.warn("Fallback: patient-profile-service indisponible, retour liste vide");
        return PageResponse.<PatientSummaryDTO>builder()
                .content(Collections.emptyList())
                .number(page).size(size)
                .totalElements(0).totalPages(0)
                .last(true)
                .build();
    }

    @Override
    public PatientSummaryDTO getPatient(UUID id) {
        log.warn("Fallback: patient-profile-service indisponible pour patient {}", id);
        return null;
    }

    @Override
    public java.util.List<PatientSummaryDTO> getByPathologyType(String type) {
        log.warn("Fallback: patient-profile-service indisponible pour pathologie {}", type);
        return Collections.emptyList();
    }
}
