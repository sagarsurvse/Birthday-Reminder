package com.example.sagar.birthday;

/**
 * Created by Sagar on 22-Jan-2022.
 */
public class model {
    private int id;
    private String name;
    private String dob;
    private byte[] image;

    public model(int id, String name, String dob, byte[] image) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
