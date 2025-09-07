package com.tutoring.Tutorverse.Repository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tutoring.Tutorverse.Model.EnrollmentEntity;

public interface EnrollRepository extends JpaRepository<EnrollmentEntity, UUID> {

    // Custom query methods (if needed) can be defined here
}
