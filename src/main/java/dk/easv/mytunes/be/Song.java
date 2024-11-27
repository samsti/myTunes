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

    public Song(int id, String title, String artist, Time duration, String filePath, int category) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.duration = new SimpleStringProperty(duration.toString());
        this.filePath = new SimpleStringProperty(filePath);
        this.category = new SimpleIntegerProperty(category);
    }

    // Getters for properties
    public IntegerProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty artistProperty() { return artist; }
    public StringProperty durationProperty() { return duration; }
    public StringProperty filePathProperty() { return filePath; }
    public IntegerProperty categoryProperty() { return category; }

    public int getId(){
        return id.get();
    }

    public String getTitle(){
        return title.get();
    }

    public String getArtist(){
        return artist.get();
    }


    public String getFilePath(){
        return filePath.get();
    }

    public int getCategory(){
        return category.get();
    }

    @Override
    public String toString() {
        return getTitle() + " " + getArtist();
    }

}
