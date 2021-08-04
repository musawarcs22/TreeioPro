package com.example.treeiopro;

public class Upload {
    private String mlatitude;
    private String mlongitude;
    private String mImageUrl;
    public Upload() {
        //empty constructor needed
    }


    public Upload(String latitude, String longitude, String imageUrl) {
        if (latitude.trim().equals("")) {
            latitude = "No Name";
            longitude = "No Name";
        }
        mlatitude = latitude;
        mlongitude = longitude;
        mImageUrl = imageUrl;
    }

    public String getMlatitude() {
        return mlatitude;
    }

    public void setMlatitude(String mlatitude) {
        this.mlatitude = mlatitude;
    }

    public String getMlongitude() {
        return mlongitude;
    }

    public void setMlongitude(String mlongitude) {
        this.mlongitude = mlongitude;
    }
    public String getImageUrl() {
        return mImageUrl;
    }
    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}