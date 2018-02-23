package com.revolut.moneytransfer.exception;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(String message) {
        super(message);
    }

}
