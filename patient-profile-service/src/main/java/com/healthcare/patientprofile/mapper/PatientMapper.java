package com.healthcare.patientprofile.mapper;

import com.healthcare.patientprofile.dto.PatientDTO;
import com.healthcare.patientprofile.dto.PathologyDTO;
import com.healthcare.patientprofile.dto.TreatmentDTO;
import com.healthcare.patientprofile.entity.Patient;
import com.healthcare.patientprofile.entity.Pathology;
import com.healthcare.patientprofile.entity.Treatment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PatientMapper {

    PatientDTO toDto(Patient patient);

    @Mapping(target = "pathologies", ignore = true)
    @Mapping(target = "treatments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Patient toEntity(PatientDTO dto);

    @Mapping(target = "patientId", source = "patient.id")
    PathologyDTO toPathologyDto(Pathology pathology);

    @Mapping(target = "patient", ignore = true)
    Pathology toPathologyEntity(PathologyDTO dto);

    @Mapping(target = "patientId", source = "patient.id")
    TreatmentDTO toTreatmentDto(Treatment treatment);

    @Mapping(target = "patient", ignore = true)
    Treatment toTreatmentEntity(TreatmentDTO dto);

    List<PatientDTO> toDtoList(List<Patient> patients);
    List<PathologyDTO> toPathologyDtoList(List<Pathology> pathologies);
    List<TreatmentDTO> toTreatmentDtoList(List<Treatment> treatments);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pathologies", ignore = true)
    @Mapping(target = "treatments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePatientFromDto(PatientDTO dto, @MappingTarget Patient patient);
}
