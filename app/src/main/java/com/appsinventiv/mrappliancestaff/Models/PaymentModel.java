package com.appsinventiv.mrappliancestaff.Models;

public class PaymentModel {
    String id;
    int price;
    String date;

    public PaymentModel() {

    }

    public PaymentModel(String id, int price, String date) {
        this.id = id;
        this.price = price;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
