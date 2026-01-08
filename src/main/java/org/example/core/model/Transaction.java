package org.example.core.model;

import java.time.LocalDateTime;

public class Transaction {
    public String category;
    public double amount;
    public TransactionType type;
    public String date;

    public Transaction(String category, double amount, TransactionType type) {
        this.category = category;
        this.amount = amount;
        this.type = type;
        this.date = LocalDateTime.now().toString();
    }
}
