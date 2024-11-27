package dk.easv.mytunes.bll;

import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.DALManager;
import dk.easv.mytunes.exceptions.DBException;

import java.util.List;

public class BLLManager {
    //private double volume;
    private final DALManager dalManager = new DALManager();

    public List<Song> getAllSongs() throws DBException {
        return dalManager.getAllSongs();
    }

//    public void setVolume(double volume) {
//        if(volume < 0.0 || volume > 1.0) {
//            throw new IllegalArgumentException("Volume must be between 0.0 and 1.0");
//        }
//
//        this.volume = volume;
//    }
//    public double getVolume() {
//        return volume;
//    }
}
