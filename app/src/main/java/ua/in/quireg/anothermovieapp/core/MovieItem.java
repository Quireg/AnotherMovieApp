package ua.in.quireg.anothermovieapp.core;


import java.net.URI;

public class MovieItem {
    private String name;
    private String id;
    private double rating;
    private String imagePreview;
    private String imageFullSize;

    public MovieItem(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public MovieItem(String name, String id, double rating, String imagePreview, String imageFullSize) {
        this.name = name;
        this.id = id;
        this.rating = rating;
        this.imagePreview = imagePreview;
        this.imageFullSize = imageFullSize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImagePreview() {
        return imagePreview;
    }

    public void setImagePreview(String imagePreview) {
        this.imagePreview = imagePreview;
    }

    public String getImageFullSize() {
        return imageFullSize;
    }

    public void setImageFullSize(String imageFullSize) {
        this.imageFullSize = imageFullSize;
    }


}
