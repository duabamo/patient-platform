package com.healthcare.patientprofile.repository;

import com.healthcare.patientprofile.entity.Pathology;
import com.healthcare.patientprofile.entity.Pathology.PathologyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PathologyRepository extends JpaRepository<Pathology, UUID> {

    List<Pathology> findByPatientId(UUID patientId);

    List<Pathology> findByPatientIdAndActiveTrue(UUID patientId);

    List<Pathology> findByType(PathologyType type);

    long countByType(PathologyType type);
}
