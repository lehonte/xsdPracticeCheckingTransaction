package org.example.services;

import org.example.enums.TransactionStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CheckingService {

    @Transactional(propagation = Propagation.REQUIRED)
    public TransactionStatus checking(String phoneNumber) {

        return TransactionStatus.ACCEPTED;
    }
}
