package com.abcd.bncserver.Model;

import java.util.List;

/**
 * Created by Karan Patel on 04-02-2018.
 */

public class Request {
    private String name;
    private String phone;
    private String address;
    private String total;
    private String status;
    private List<Order> foods;

    public Request(){

    }

    public Request(String name, String phone, String address, String total, String status, List<Order> foods) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.total = total;
        this.status = status;
        this.foods = foods;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
