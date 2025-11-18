package com.example.financemanager.classes;

public class TransactionData {
    private String title;
    private double amount;
    private String date;
    private String type;
    private String category;

    public TransactionData() {
    }

    public TransactionData(String title, double amount, String date, String type, String category) {
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
