package com.MyBookCorner.model;

import java.sql.Timestamp;

public class Book {
    private int id;
    private String title;
    private String author;
    private String description;
    private int addedByUserId;
    private Timestamp createdAt;
    private double averageRating;

    public Book() {
    }

    public Book(String title, String author, String description, int addedByUserId) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.addedByUserId = addedByUserId;
    }

    public Book(int id, String title, String author, String description, int addedByUserId, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.addedByUserId = addedByUserId;
        this.createdAt = createdAt;
    }

    public Book(int id, String title, String author, String description, int addedByUserId, Timestamp createdAt, double averageRating) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.addedByUserId = addedByUserId;
        this.createdAt = createdAt;
        this.averageRating = averageRating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAddedByUserId() {
        return addedByUserId;
    }

    public void setAddedByUserId(int addedByUserId) {
        this.addedByUserId = addedByUserId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", addedByUserId=" + addedByUserId +
                ", createdAt=" + createdAt +
                ", averageRating=" + averageRating +
                '}';
    }
}
