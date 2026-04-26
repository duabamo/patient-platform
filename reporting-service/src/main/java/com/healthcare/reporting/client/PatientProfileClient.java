package com.healthcare.reporting.client;

import com.healthcare.reporting.dto.PageResponse;
import com.healthcare.reporting.dto.PatientSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "patient-profile-service",
        url = "${patient-profile.service.url:http://patient-profile-service:8081}",
        fallback = PatientProfileClientFallback.class
)
public interface PatientProfileClient {

    @GetMapping("/api/v1/patients")
    PageResponse<PatientSummaryDTO> listPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size);

    @GetMapping("/api/v1/patients/{id}")
    PatientSummaryDTO getPatient(@PathVariable("id") UUID id);

    @GetMapping("/api/v1/patients/by-pathology/{type}")
    List<PatientSummaryDTO> getByPathologyType(@PathVariable("type") String type);
}
