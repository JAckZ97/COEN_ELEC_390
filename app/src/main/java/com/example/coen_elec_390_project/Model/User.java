package com.example.coen_elec_390_project.Model;

public class User {
    private int id;
    private String fullname;
    private String email;
    private String password;

    public User() {

    }

    public User(int id, String fullname, String email, String password) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
    }

    public User(String fullname, String email, String password) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
