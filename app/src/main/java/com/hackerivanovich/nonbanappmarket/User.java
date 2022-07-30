package com.hackerivanovich.nonbanappmarket;

public class User {
    public String id, email, password, name, surname, location;

    public User() {

    }

    public User(String id, String email, String password, String name, String surname, String location) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.location = location;
    }
}
