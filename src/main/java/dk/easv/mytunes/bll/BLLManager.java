package dk.easv.mytunes.bll;

import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.DALManager;
import dk.easv.mytunes.exceptions.DBException;

import java.util.List;

public class BLLManager {
    private final DALManager dalManager = new DALManager();

    public List<Song> getAllSongs() throws DBException {
        return dalManager.getAllSongs();
    }
}
