package dk.easv.mytunes.be;

import java.sql.Time;

public class Song {
    private int id;
    private String title;
    private String artist;
    private Time duration;
    private String filePath;
    private int Category;

    public Song(int id, String title, String artist, Time duration, String filePath, int Category){
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.filePath = filePath;
        this.Category = Category;
    }

    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getArtist(){
        return artist;
    }

    public Time getDuration(){
        return duration;
    }

    public String getFilePath(){
        return filePath;
    }

    public int getCategory(){
        return Category;
    }

}
