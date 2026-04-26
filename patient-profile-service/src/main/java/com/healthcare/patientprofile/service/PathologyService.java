package com.healthcare.patientprofile.service;

import com.healthcare.patientprofile.dto.PathologyDTO;
import com.healthcare.patientprofile.entity.Pathology;
import com.healthcare.patientprofile.entity.Patient;
import com.healthcare.patientprofile.exception.ResourceNotFoundException;
import com.healthcare.patientprofile.mapper.PatientMapper;
import com.healthcare.patientprofile.repository.PathologyRepository;
import com.healthcare.patientprofile.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PathologyService {

    private final PathologyRepository pathologyRepository;
    private final PatientRepository patientRepository;
    private final PatientMapper mapper;

    public PathologyDTO addToPatient(UUID patientId, PathologyDTO dto) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", patientId));
        Pathology pathology = mapper.toPathologyEntity(dto);
        pathology.setPatient(patient);
        Pathology saved = pathologyRepository.save(pathology);
        log.info("Pathologie {} ajoutee au patient {}", saved.getType(), patientId);
        return mapper.toPathologyDto(saved);
    }

    @Transactional(readOnly = true)
    public List<PathologyDTO> findByPatient(UUID patientId) {
        return mapper.toPathologyDtoList(pathologyRepository.findByPatientId(patientId));
    }

    @Transactional(readOnly = true)
    public List<PathologyDTO> findActiveByPatient(UUID patientId) {
        return mapper.toPathologyDtoList(pathologyRepository.findByPatientIdAndActiveTrue(patientId));
    }

    public PathologyDTO update(UUID id, PathologyDTO dto) {
        Pathology existing = pathologyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pathology", id));
        if (dto.getType() != null) existing.setType(dto.getType());
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getIcd10Code() != null) existing.setIcd10Code(dto.getIcd10Code());
        if (dto.getSeverity() != null) existing.setSeverity(dto.getSeverity());
        if (dto.getDiagnosisDate() != null) existing.setDiagnosisDate(dto.getDiagnosisDate());
        if (dto.getNotes() != null) existing.setNotes(dto.getNotes());
        existing.setActive(dto.isActive());
        return mapper.toPathologyDto(pathologyRepository.save(existing));
    }

    public void delete(UUID id) {
        if (!pathologyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pathology", id);
        }
        pathologyRepository.deleteById(id);
    }
}
