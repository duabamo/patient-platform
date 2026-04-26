package com.healthcare.patientprofile;

import com.healthcare.patientprofile.dto.PatientDTO;
import com.healthcare.patientprofile.entity.Patient;
import com.healthcare.patientprofile.exception.DuplicateResourceException;
import com.healthcare.patientprofile.exception.ResourceNotFoundException;
import com.healthcare.patientprofile.mapper.PatientMapper;
import com.healthcare.patientprofile.repository.PatientRepository;
import com.healthcare.patientprofile.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock private PatientRepository patientRepository;
    @Mock private PatientMapper patientMapper;
    @InjectMocks private PatientService patientService;

    private PatientDTO dto;
    private Patient entity;

    @BeforeEach
    void setUp() {
        dto = PatientDTO.builder()
                .firstName("Jean").lastName("Dupont")
                .socialSecurityNumber("1234567890123")
                .birthDate(LocalDate.of(1980, 1, 1))
                .gender(Patient.Gender.MALE)
                .build();
        entity = Patient.builder()
                .id(UUID.randomUUID())
                .firstName("Jean").lastName("Dupont")
                .socialSecurityNumber("1234567890123")
                .birthDate(LocalDate.of(1980, 1, 1))
                .gender(Patient.Gender.MALE)
                .build();
    }

    @Test
    void create_shouldSaveNewPatient() {
        when(patientRepository.existsBySocialSecurityNumber(any())).thenReturn(false);
        when(patientMapper.toEntity(any())).thenReturn(entity);
        when(patientRepository.save(any())).thenReturn(entity);
        when(patientMapper.toDto(any())).thenReturn(dto);

        PatientDTO result = patientService.create(dto);

        assertThat(result).isNotNull();
        verify(patientRepository).save(any());
    }

    @Test
    void create_shouldThrowWhenDuplicate() {
        when(patientRepository.existsBySocialSecurityNumber(any())).thenReturn(true);
        assertThatThrownBy(() -> patientService.create(dto))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> patientService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
