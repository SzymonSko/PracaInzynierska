package com.example.inzynierka;
public class UserDetails {
    public String name, mail, join_date, pass;
    public UserDetails(){};

    public UserDetails(String TextProfile, String TextMail, String TextDate, String TextPass){

        this.name = TextProfile;
        this.mail = TextMail;
        this.join_date = TextDate;
        this.pass = TextPass;

    }
}
