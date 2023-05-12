package com.example.BookStore_app.model;

import java.io.Serializable;

public class Cart implements Serializable {
    private int id;
    private User user;

    public Cart(int id, User user) {
        this.id = id;
        this.user = user;
    }

    public Cart(User user) {
        this.user = user;
    }

    public Cart() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", user=" + user +
                '}';
    }
}
