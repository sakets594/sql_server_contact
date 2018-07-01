package com.example.sks.login_register;

import android.util.Patterns;

import net.sourceforge.jtds.jdbc.DateTime;

/**
 * Created by sks on 29/6/18.
 */

public class Contact {
    private String name,email,number1,number2,website,city,state,country;
    private int contactid,addedby;
    DateTime last;
    // data which have been synced with sql server

    public int getAddedby() {
        return addedby;
    }

    public void setAddedby(int addedby) {
        this.addedby = addedby;
    }
    public Contact(){}

    public Contact(String name, String email, String number1, String website, String city, String state, String country, int contactid, int addedby) {
        this.name = name;
        this.email = email;
        this.number1 = number1;
        this.contactid = contactid;
        this.website = website;
        this.city = city;
        this.state = state;
        this.country = country;
        this.addedby=addedby;
    }

//for new data addition
    public Contact(String name, String email, String number1, String website, String city, String state, String country) {
        this.name = name;
        this.email = email;
        this.number1 = number1;
        this.website = website;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getContactid() {
        return contactid;
    }

    public void setContactid(int contactid) {
        this.contactid = contactid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber1() {
        return number1;
    }

    public void setNumber1(String number1) {
        this.number1 = number1;
    }

    public String getNumber2() {
        return number2;
    }

    public void setNumber2(String number2) {
        this.number2 = number2;
    }

    public String getWebsite() {
        return website;
    }


    public void setWebsite(String website) {
        this.website = website;
    }

    public String validate(){
        if(this.name.equals("")||this.number1.equals("")){
            return new String("Name and Number are mandatory");
        }
        else if(!Patterns.PHONE.matcher(number1).matches())
        {
            return new String("invalid number");
        }
        else if(!email.equals("")&& !android.util.Patterns.EMAIL_ADDRESS.matcher(this.email).matches())
        {
            return new String("Invalid Email Address");
        }
        else if(!website.equals("")&& !android.util.Patterns.WEB_URL.matcher(website).matches())
        {
            return new String("Invalid Website adress");
        }
        return "";

    }
}
