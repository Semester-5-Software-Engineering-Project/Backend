package com.tutoring.Tutorverse.Repository;

import com.tutoring.Tutorverse.Model.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<WalletEntity, UUID> {
    Optional<WalletEntity> findByTutorId(UUID tutorId);
}