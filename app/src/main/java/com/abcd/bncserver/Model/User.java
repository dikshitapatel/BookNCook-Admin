package com.abcd.bncserver.Model;

/**
 * Created by Karan Patel on 21-01-2018.
 */

public class User {

    private String Name,Password,Phone,IsStaff;

    public User(String name, String password, String phone, String isStaff) {
        Name = name;
        Password = password;
        Phone = phone;
        IsStaff = isStaff;
    }

    public User() {

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }
}
