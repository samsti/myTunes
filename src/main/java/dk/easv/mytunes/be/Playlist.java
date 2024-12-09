package dk.easv.mytunes.be;

import java.sql.Time;

public class Playlist {

    private int id;
    private String name;
    private Time totalDuration;
    private int numberOfSongs;

    public Playlist(int id, String name, Time totalDuration, int numberOfSongs){
        this.id = id;
        this.name = name;
        this.totalDuration = totalDuration;
        this.numberOfSongs = numberOfSongs;
    }

    public Playlist(String name){
        this.name = name;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getTotalDuration(){
        return String.valueOf(totalDuration);
    }

    public void setTotalDuration(String totalDuration){
        this.totalDuration = Time.valueOf(totalDuration);
    }

    public int getNumberOfSongs(){
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs){
        this.numberOfSongs = numberOfSongs;
    }

    @Override
    public String toString() {
        return id + " " + name;
    }

}
