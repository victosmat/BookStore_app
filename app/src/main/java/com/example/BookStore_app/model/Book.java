package com.example.BookStore_app.model;

import java.io.Serializable;

public class Book implements Serializable {
    private int id;
    private String name, author, category, price, urlImage;

    public Book(int id, String name, String author, String category, String price,String urlImage) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.category = category;
        this.price = price;
        this.urlImage = urlImage;
    }

    public Book(String name, String author, String category, String price, String urlImage) {
        this.name = name;
        this.author = author;
        this.category = category;
        this.price = price;
        this.urlImage = urlImage;
    }

    public Book() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", price='" + price + '\'' +
                ", urlImage='" + urlImage + '\'' +
                '}';
    }
}
