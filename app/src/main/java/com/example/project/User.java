package com.example.project;

public class User {
    public String name;
    public String status;
    public String image;
    public String date;
    public String request_type;

    public String getDate() {
        return date;
    }

    public String getRequestType() {
        return request_type;
    }

    public void setRequestType(String request_Type) {
        request_type = request_Type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User() {
    }

    public User(String name, String status, String image) {
        this.name = name;
        this.status = status;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
