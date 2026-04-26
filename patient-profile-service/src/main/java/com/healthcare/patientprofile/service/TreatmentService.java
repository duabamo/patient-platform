package com.healthcare.patientprofile.service;

import com.healthcare.patientprofile.dto.TreatmentDTO;
import com.healthcare.patientprofile.entity.Patient;
import com.healthcare.patientprofile.entity.Treatment;
import com.healthcare.patientprofile.exception.ResourceNotFoundException;
import com.healthcare.patientprofile.mapper.PatientMapper;
import com.healthcare.patientprofile.repository.PatientRepository;
import com.healthcare.patientprofile.repository.TreatmentRepository;
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
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final PatientRepository patientRepository;
    private final PatientMapper mapper;

    public TreatmentDTO addToPatient(UUID patientId, TreatmentDTO dto) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", patientId));
        Treatment treatment = mapper.toTreatmentEntity(dto);
        treatment.setPatient(patient);
        Treatment saved = treatmentRepository.save(treatment);
        log.info("Traitement {} ajoute au patient {}", saved.getMedicationName(), patientId);
        return mapper.toTreatmentDto(saved);
    }

    @Transactional(readOnly = true)
    public List<TreatmentDTO> findByPatient(UUID patientId) {
        return mapper.toTreatmentDtoList(treatmentRepository.findByPatientId(patientId));
    }

    @Transactional(readOnly = true)
    public List<TreatmentDTO> findActiveByPatient(UUID patientId) {
        return mapper.toTreatmentDtoList(treatmentRepository.findByPatientIdAndActiveTrue(patientId));
    }

    public TreatmentDTO update(UUID id, TreatmentDTO dto) {
        Treatment existing = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment", id));
        if (dto.getMedicationName() != null) existing.setMedicationName(dto.getMedicationName());
        if (dto.getActiveIngredient() != null) existing.setActiveIngredient(dto.getActiveIngredient());
        if (dto.getDosage() != null) existing.setDosage(dto.getDosage());
        if (dto.getFrequency() != null) existing.setFrequency(dto.getFrequency());
        if (dto.getRoute() != null) existing.setRoute(dto.getRoute());
        if (dto.getStartDate() != null) existing.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) existing.setEndDate(dto.getEndDate());
        if (dto.getPrescribingDoctor() != null) existing.setPrescribingDoctor(dto.getPrescribingDoctor());
        if (dto.getInstructions() != null) existing.setInstructions(dto.getInstructions());
        existing.setActive(dto.isActive());
        return mapper.toTreatmentDto(treatmentRepository.save(existing));
    }

    public void delete(UUID id) {
        if (!treatmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Treatment", id);
        }
        treatmentRepository.deleteById(id);
    }
}
