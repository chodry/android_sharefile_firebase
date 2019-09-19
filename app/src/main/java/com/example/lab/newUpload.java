package com.example.lab;

import com.google.firebase.database.Exclude;

public class newUpload {

    private String mImageUrl;
    private  String mKey;

    public newUpload() {
        //empty constructor needed
    }

    public newUpload(String imageUrl) {

        mImageUrl = imageUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }
}
