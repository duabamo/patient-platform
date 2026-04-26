package com.healthcare.patientprofile.repository;

import com.healthcare.patientprofile.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, UUID> {

    List<Treatment> findByPatientId(UUID patientId);

    List<Treatment> findByPatientIdAndActiveTrue(UUID patientId);
}
