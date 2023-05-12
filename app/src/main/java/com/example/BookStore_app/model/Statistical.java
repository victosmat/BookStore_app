package com.example.BookStore_app.model;

import java.io.Serializable;

public class Statistical implements Serializable {
    private int id;
    private Book book;
    private int total;

    public Statistical() {
    }

    public Statistical(int id, Book book, int total) {
        this.id = id;
        this.book = book;
        this.total = total;
    }

    public Statistical(Book book, int total) {
        this.book = book;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Statistical{" +
                "id=" + id +
                ", book=" + book +
                ", total=" + total +
                '}';
    }
}
