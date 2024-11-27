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

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public Time getTotalDuration(){
        return totalDuration;
    }

    public int getNumberOfSongs(){
        return numberOfSongs;
    }

    @Override
    public String toString() {
        return name;
    }
}
