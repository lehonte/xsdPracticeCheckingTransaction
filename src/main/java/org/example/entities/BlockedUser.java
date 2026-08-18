package org.example.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "blocked_owners")
@Getter
@Setter
@NoArgsConstructor
public class BlockedUser {

    @Id
    @Column(name = "owner")
    private String owner;

    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;

    public BlockedUser(String owner) {
        this.owner = owner;
        this.blockedAt = LocalDateTime.now();
    }
}
