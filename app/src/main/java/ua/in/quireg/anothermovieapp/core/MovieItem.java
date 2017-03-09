package ua.in.quireg.anothermovieapp.core;


import java.io.Serializable;

public class MovieItem implements Serializable {
    private String originalTitle;
    private String title;
    private String id;
    private String description;
    private double rating;
    private boolean adult;
    private String imageBackdrop;
    private String imageFullSize;

    public MovieItem(String originalTitle, String title, String id, String description, boolean adult, double rating, String imageBackdrop, String imageFullSize) {
        this.originalTitle = originalTitle;
        this.title = title;
        this.id = id;
        this.description = description;
        this.adult = adult;
        this.rating = rating;
        this.imageBackdrop = imageBackdrop;
        this.imageFullSize = imageFullSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImageBackdrop() {
        return imageBackdrop;
    }

    public void setImageBackdrop(String imageBackdrop) {
        this.imageBackdrop = imageBackdrop;
    }

    public String getImageFullSize() {
        return imageFullSize;
    }

    public void setImageFullSize(String imageFullSize) {
        this.imageFullSize = imageFullSize;
    }


}
