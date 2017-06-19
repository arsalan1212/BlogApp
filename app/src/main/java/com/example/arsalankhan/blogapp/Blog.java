package com.example.arsalankhan.blogapp;

/**
 * Created by Arsalan khan on 6/15/2017.
 */

public class Blog {

    private String image;
    private String title;
    private String description;


    private String userName;

    public Blog(){
        //TODO: Empty constructor important for Firebase otherwise App crash
    }

    public Blog(String image, String title, String description,String userName) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.userName=userName;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUserName() {
        return userName;
    }

}
