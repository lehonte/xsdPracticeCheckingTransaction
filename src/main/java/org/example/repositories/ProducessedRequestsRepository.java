package org.example.repositories;

import org.example.eventEntities.ProducessedTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProducessedRequestsRepository extends JpaRepository<ProducessedTransactions, String> {
    Boolean existsByTransactionNumber(String transactionNumber);
}
