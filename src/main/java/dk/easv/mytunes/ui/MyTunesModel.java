package dk.easv.mytunes.ui;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.bll.BLLManager;
import dk.easv.mytunes.exceptions.DBException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MyTunesModel {
    private final BLLManager manager = new BLLManager();
    private final ObservableList<Song> songs = FXCollections.observableArrayList();
    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();
    private final ObservableList<Song> songsOnPlaylist = FXCollections.observableArrayList();

    public void loadSongs() throws DBException {
        songs.setAll(manager.getAllSongs());
    }

    public ObservableList<Song> getSongs() {
        return songs;
    }

    public void loadPlaylists() throws DBException {
        playlists.setAll(manager.getAllPlaylists());
    }

    public ObservableList<Playlist> getPlaylists() {
        return playlists;
    }

    public void loadSongsOnPlaylist(int playlistId) throws DBException {
        songsOnPlaylist.setAll(manager.getSongsOnPlaylist(playlistId));
    }

    public ObservableList<Song> getSongsOnPlaylist() {
        return songsOnPlaylist;
    }

    public void createPlaylist(String title) throws DBException {
        Playlist newPlaylist = manager.addPlaylist(new Playlist(title));
        playlists.add(newPlaylist);
    }
}
