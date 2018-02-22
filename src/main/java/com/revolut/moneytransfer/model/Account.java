package com.revolut.moneytransfer.model;

import java.util.UUID;

public class Account {

    private UUID id;
    private String name;
    private Long balance;

    public Account() {}

    public Account(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.balance = 0L;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "id: " + id + ", name: " + name + ", balance: " + balance;
    }
}
