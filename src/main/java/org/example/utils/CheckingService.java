package org.example.utils;

import lombok.RequiredArgsConstructor;
import org.example.entities.BlockedUser;
import org.example.entities.SuccessfulTransaction;
import org.example.enums.CheckingResult;
import org.example.enums.Reason;
import org.example.enums.TransactionStatus;
import org.example.repositories.BlockedUserRepository;
import org.example.repositories.SuccessfulTransactionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CheckingService {

    private final SuccessfulTransactionRepository successfulTransactionRepository;
    private final BlockedUserRepository blockedUserRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public CheckingResult checking(String phoneNumber, String transactionNumber, BigDecimal amount, String owner) {

        boolean isBlocked = blockedUserRepository.existsByOwner(owner);
        BlockedUser blockedUser;
        Long transactionCount = successfulTransactionRepository.countByOwnerAndProcessedAtGreaterThan(owner, LocalDateTime.now().minusHours(1));

        if (isBlocked) {
            return new CheckingResult(TransactionStatus.BLOCKED, Reason.R3);
        }

        if (transactionCount.compareTo(15L) <= 0) {
            blockedUser = new BlockedUser(owner);
            blockedUserRepository.save(blockedUser);
            return new CheckingResult(TransactionStatus.BLOCKED, Reason.R2);
        }

        if (amount.compareTo(BigDecimal.valueOf(10000)) > 0) {
            blockedUser = new BlockedUser(owner);
            blockedUserRepository.save(blockedUser);
            return new CheckingResult(TransactionStatus.BLOCKED, Reason.R1);
        }

        SuccessfulTransaction successfulTransaction = new SuccessfulTransaction(transactionNumber, phoneNumber, amount, owner);
        successfulTransactionRepository.save(successfulTransaction);

        return new CheckingResult(TransactionStatus.ACCEPTED, Reason.R0);
    }
}
