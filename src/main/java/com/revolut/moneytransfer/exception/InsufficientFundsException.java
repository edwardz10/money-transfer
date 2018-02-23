package com.revolut.moneytransfer.exception;

public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(String message) {
        super(message);
    }

}
