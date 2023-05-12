package com.example.BookStore_app.model;

import java.io.Serializable;

public class CartBook implements Serializable {
    private int id;
    private Book book;
    private Cart cart;
    private int quantity;
    private int isChecked;

    public CartBook(int id, Book book, Cart cart, int quantity, int isChecked) {
        this.id = id;
        this.book = book;
        this.cart = cart;
        this.quantity = quantity;
        this.isChecked = isChecked;
    }

    public CartBook(Book book, Cart cart, int quantity, int isChecked) {
        this.book = book;
        this.cart = cart;
        this.quantity = quantity;
        this.isChecked = isChecked;
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

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getChecked() {
        return isChecked;
    }

    public void setChecked(int checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "CartBook{" +
                "id=" + id +
                ", book=" + book +
                ", cart=" + cart +
                ", quantity=" + quantity +
                ", isChecked=" + isChecked +
                '}';
    }
}
