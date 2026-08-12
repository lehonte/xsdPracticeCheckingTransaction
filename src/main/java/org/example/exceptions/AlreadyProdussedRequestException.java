package org.example.exceptions;

public class AlreadyProdussedRequestException extends RuntimeException {
    public AlreadyProdussedRequestException(String message) {
        super(message);
    }
}
