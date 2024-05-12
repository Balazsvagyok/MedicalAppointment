package com.example.medicalappointment;

public class DoctorItem {
    private String name;
    private String info;
    private String phone;
    private float ratedInfo;
    // private final int imageResource;

    public DoctorItem(String name, String info, String phone, float ratedInfo /*, int imageResource*/) {
        this.name = name;
        this.info = info;
        this.phone = phone;
        this.ratedInfo = ratedInfo;
        // this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }
    public String getInfo() {
        return info;
    }
    public String getPhone() {
        return phone;
    }
    public float getRatedInfo() {
        return ratedInfo;
    }

//    public int getImageResource() {
//        return imageResource;
//    }
}
