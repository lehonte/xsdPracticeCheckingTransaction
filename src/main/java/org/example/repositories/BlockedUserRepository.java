package org.example.repositories;

import org.example.entities.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUser, String> {
    boolean existsByOwner(String owner);
}
