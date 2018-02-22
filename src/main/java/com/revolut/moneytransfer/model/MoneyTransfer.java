package com.revolut.moneytransfer.model;

import java.util.UUID;

public class MoneyTransfer {

    private Long amount;
    private UUID from;
    private UUID to;

    public MoneyTransfer() {}

    public MoneyTransfer(Long amount) {
        this.amount = amount;
    }

    public void setFrom(UUID from) {
        this.from = from;
    }

    public void setTo(UUID to) {
        this.to = to;
    }

    public UUID getFrom() {

        return from;
    }

    public UUID getTo() {
        return to;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
