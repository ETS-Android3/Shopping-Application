package com.example.ecommerce;

import java.io.Serializable;

public class CustomerInfo implements Serializable {

    private String id;
    private String name;
    private String username;
    private String password;
    private String gender;
    private String birthdate;
    private String job;

    public CustomerInfo(String id, String name, String username, String password, String gender, String birthdate, String job)
    {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.birthdate = birthdate;
        this.job = job;
    }

    public String[] getInfo()
    {
        String[] info = new String[7];
        info[0] = id;
        info[1] = name;
        info[2] = username;
        info[3] = password;
        info[4] = gender;
        info[5] = birthdate;
        info[6] = job;

        return info;
    }

    public String getID()
    {
        return id;
    }
}
