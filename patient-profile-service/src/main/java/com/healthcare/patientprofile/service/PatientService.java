package com.healthcare.patientprofile.service;

import com.healthcare.patientprofile.dto.PatientDTO;
import com.healthcare.patientprofile.entity.Patient;
import com.healthcare.patientprofile.exception.DuplicateResourceException;
import com.healthcare.patientprofile.exception.ResourceNotFoundException;
import com.healthcare.patientprofile.mapper.PatientMapper;
import com.healthcare.patientprofile.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientDTO create(PatientDTO dto) {
        log.info("Creation d'un patient: {}", dto.getSocialSecurityNumber());
        if (patientRepository.existsBySocialSecurityNumber(dto.getSocialSecurityNumber())) {
            throw new DuplicateResourceException(
                    "Un patient avec le numero de securite sociale " + dto.getSocialSecurityNumber() + " existe deja");
        }
        Patient patient = patientMapper.toEntity(dto);
        Patient saved = patientRepository.save(patient);
        return patientMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public PatientDTO findById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        return patientMapper.toDto(patient);
    }

    @Transactional(readOnly = true)
    public Page<PatientDTO> findAll(Pageable pageable) {
        return patientRepository.findAll(pageable).map(patientMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PatientDTO> search(String query, Pageable pageable) {
        return patientRepository
                .findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(query, query, pageable)
                .map(patientMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> findByPathologyType(String type) {
        return patientMapper.toDtoList(patientRepository.findByPathologyType(type));
    }

    public PatientDTO update(UUID id, PatientDTO dto) {
        log.info("Mise a jour patient {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        patientMapper.updatePatientFromDto(dto, patient);
        return patientMapper.toDto(patientRepository.save(patient));
    }

    public void delete(UUID id) {
        log.info("Suppression patient {}", id);
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient", id);
        }
        patientRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return patientRepository.count();
    }
}
