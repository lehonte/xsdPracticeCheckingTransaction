package org.example.repositories;

import org.example.entities.SuccessfulTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SuccessfulTransactionRepository extends JpaRepository<SuccessfulTransaction, String> {
    Long countByOwnerAndProcessedAtGreaterThan(String owner, LocalDateTime processedAt);
}
