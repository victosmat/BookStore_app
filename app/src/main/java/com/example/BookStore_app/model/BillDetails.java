package com.example.BookStore_app.model;

import java.io.Serializable;

public class BillDetails implements Serializable {
    private int id;
    private Bill bill;
    private Book book;
    private int quantity;

    public BillDetails(int id, Bill bill, Book book, int quantity) {
        this.id = id;
        this.bill = bill;
        this.book = book;
        this.quantity = quantity;
    }

    public BillDetails(Bill bill, Book book, int quantity) {
        this.bill = bill;
        this.book = book;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "BillDetails{" +
                "id=" + id +
                ", bill=" + bill +
                ", book=" + book +
                ", quantity=" + quantity +
                '}';
    }
}
