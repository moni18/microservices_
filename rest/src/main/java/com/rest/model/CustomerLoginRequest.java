package com.rest.model;

public class CustomerLoginRequest {
    private String username;
    private String password;

    public CustomerLoginRequest() {
        // Пустой конструктор для сериализации/десериализации JSON
    }

    public CustomerLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
