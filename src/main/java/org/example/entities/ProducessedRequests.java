package org.example.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "producessd_requests")
@NoArgsConstructor
@Getter
@Setter
public class ProducessedRequests {

    @Id
    @Column(name = "request_id", length = 36)
    private String requestId;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public ProducessedRequests(String requestId) {
        this.requestId = requestId;
        this.processedAt = LocalDateTime.now();
    }
}
