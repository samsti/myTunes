package dk.easv.mytunes.ui;

import dk.easv.mytunes.be.Category;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.bll.BLLManager;
import dk.easv.mytunes.exceptions.DBException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Time;
import java.util.List;

public class MyTunesModel {
    private final BLLManager manager = new BLLManager();
    private final ObservableList<Song> songs = FXCollections.observableArrayList();
    private final ObservableList<Song> filteredSongs = FXCollections.observableArrayList();
    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();
    private final ObservableList<Song> songsOnPlaylist = FXCollections.observableArrayList();

    public void loadSongs() throws DBException {
        songs.setAll(manager.getAllSongs());
    }

    public ObservableList<Song> getSongs() {
        return songs;
    }

    public void loadFilteredSongs(String filter) throws DBException {
        filteredSongs.setAll(manager.getFilteredSongs(filter));
    }

    public ObservableList<Song> getFilteredSongs() {
        return filteredSongs;
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

    public void deletePlaylist(Playlist playlist) throws DBException {
        manager.deletePlaylist(playlist);
        playlists.remove(playlist);
    }

    public List<Category> getCategories() {
        return manager.getAllCategories();
    }
    public void moveSongUpInList(Song song, int playlist){
        int currentIndex = songsOnPlaylist.indexOf(song);

        if (song.getOrder() > 1 && currentIndex >= 1) {
            // Swap the items in the ObservableList
            manager.moveSongUp(song, playlist, true);
            Song prevSong = songsOnPlaylist.get(currentIndex - 1);
            songsOnPlaylist.set(currentIndex, prevSong);
            songsOnPlaylist.set(currentIndex - 1, song);
        }
    }
    public void moveSongDownInList(Song song, int playlist) {
        int currentIndex = songsOnPlaylist.indexOf(song);

        if (song.getOrder() < manager.numberOfSongsInList(playlist) && (currentIndex < songsOnPlaylist.size() - 1)) {
            // Swap the items in the ObservableList
            Song nextSong = songsOnPlaylist.get(currentIndex + 1);
            songsOnPlaylist.set(currentIndex, nextSong);
            songsOnPlaylist.set(currentIndex + 1, song);
            manager.moveSongUp(song, playlist, false);
        }
    }

    public void addSongToPlaylist(int playlistId, int songId) throws DBException {
        manager.addSongToPlaylist(playlistId, songId);
    }

    public void deleteFromPlaylist(Song selectedSongInPlaylist, Playlist selectedPlaylist) {
        if (selectedSongInPlaylist != null && selectedPlaylist != null)
            if (manager.deleteFromPlaylist(selectedSongInPlaylist, selectedPlaylist))
                songsOnPlaylist.remove(selectedSongInPlaylist);
    }
    public void deleteSong(Song selectedSong, boolean deleteFile) {
        if (selectedSong != null) {
            if (manager.deletSong(selectedSong, deleteFile))
                songs.remove(selectedSong);
            else
                throw new RuntimeException(deleteFile ?
                        "Song and/or file could not be deleted" : "Song could not be deleted from database");
        } else
            throw new RuntimeException("No song is selected");
    }

    public int addSong(String title, String artist, String filePath, Time duration, int category) {
        int songId;
        songId = manager.addSong(title, artist, filePath, duration, category);
        Song newSong = new Song(songId, title, artist, duration, filePath, category);
        songs.add(newSong);
        return songId;
    }
}