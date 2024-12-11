package dk.easv.mytunes.be;

import javafx.beans.property.*;

import java.sql.Time;

public class Song {
    private final IntegerProperty id;
    private final StringProperty title;
    private final StringProperty artist;
    private final StringProperty duration;
    private final StringProperty filePath;
    private final IntegerProperty category;
    private final StringProperty categoryName;
    private int order;

    public Song(int id, String title, String artist, Time duration, String filePath, int category, String categoryName) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.duration = new SimpleStringProperty(duration.toString());
        this.filePath = new SimpleStringProperty(filePath);
        this.category = new SimpleIntegerProperty(category);
        this.categoryName = new SimpleStringProperty(categoryName);
    }

    public Song(int id, String title, String artist, Time duration, String filePath, int category) {
        this(id, title, artist, duration, filePath, category, null);
    }

    // Getters for properties
    public IntegerProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty artistProperty() { return artist; }
    public StringProperty durationProperty() { return duration; }
    public StringProperty filePathProperty() { return filePath; }
    public IntegerProperty categoryProperty() { return category; }
    public StringProperty categoryNameProperty() { return categoryName; }

    public int getId(){
        return id.get();
    }

    public String getTitle(){
        return title.get();
    }

    public void setTitle(String title){
        this.title.set(title);
    }

    public String getArtist(){
        return artist.get();
    }

    public void setArtist(String artist){
        this.artist.set(artist);
    }

    public String getDuration(){ return duration.get(); }

    public void setDuration(Time duration){
        this.duration.set(String.valueOf(duration));
    }

    public String getFilePath(){
        return filePath.get();
    }

    public void setFilePath(String filePath){
        this.filePath.set(filePath);
    }

    public int getCategory(){
        return category.get();
    }

    public void setCategory(int category){
        this.category.set(category);
    }
    public int getOrder(){
        return order;
    }
    public void setOrder(int order){
        this.order = order;
    }

    @Override
    public String toString() {
        return getTitle() + " - " + getArtist() + "\t" + getDuration();
    }

}
