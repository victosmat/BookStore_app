package com.example.BookStore_app.model;

import java.io.Serializable;
import java.util.List;

public class Bill implements Serializable {
    private int id;
    private String date, total;
    private User user;

    public Bill(int id, String date, String total, User user) {
        this.id = id;
        this.date = date;
        this.total = total;
        this.user = user;
    }

    public Bill(String date, String total, User user) {
        this.date = date;
        this.total = total;
        this.user = user;
    }

    public Bill() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", total='" + total + '\'' +
                ", user=" + user +
                '}';
    }
}
