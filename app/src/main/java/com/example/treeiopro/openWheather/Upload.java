package com.example.treeiopro.openWheather;

public class Upload {
    private String mlatitude;
    private String mlongitude;
    private String mImageUrl;
    private String mImageTitle;
    private String mImageDiscription;
    public Upload() {
        //empty constructor needed
    }


    public Upload(String latitude, String longitude, String imageUrl,String title, String discription) {
        if (latitude.trim().equals("")) {
            latitude = "No Name";
            longitude = "No Name";
        }
        mlatitude = latitude;
        mlongitude = longitude;
        mImageUrl = imageUrl;
        setmImageTitle(title);
        setmImageDiscription(discription);
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

    public String getmImageTitle() {
        return mImageTitle;
    }

    public void setmImageTitle(String mImageTitle) {
        this.mImageTitle = mImageTitle;
    }

    public String getmImageDiscription() {
        return mImageDiscription;
    }

    public void setmImageDiscription(String mImageDiscription) {
        this.mImageDiscription = mImageDiscription;
    }
}