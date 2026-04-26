package com.healthcare.patientprofile.repository;

import com.healthcare.patientprofile.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findBySocialSecurityNumber(String socialSecurityNumber);

    boolean existsBySocialSecurityNumber(String socialSecurityNumber);

    Page<Patient> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(
            String lastName, String firstName, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Patient p JOIN p.pathologies pat WHERE pat.type = :type AND pat.active = true")
    List<Patient> findByPathologyType(String type);
}
