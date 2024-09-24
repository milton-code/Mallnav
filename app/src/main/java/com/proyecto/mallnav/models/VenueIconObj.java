package com.proyecto.mallnav.models;

public class VenueIconObj {

    private int imageDrawable = -1;
    private String categoryName = null;
    private boolean isActivated = false;

    public VenueIconObj(int imageDrawable, String categoryName) {
        this.imageDrawable = imageDrawable;
        this.categoryName = categoryName;
    }

    public int getImageDrawable() {
        return imageDrawable;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }
}
