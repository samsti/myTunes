package dk.easv.mytunes.bll;

import dk.easv.mytunes.be.Category;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.ChooseFile;
import dk.easv.mytunes.dal.DALManager;
import dk.easv.mytunes.dal.FileManager;
import dk.easv.mytunes.exceptions.DBException;
import dk.easv.mytunes.ui.MainController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;



public class BLLManager {
    //private double volume;
    private final DALManager dalManager = new DALManager();
    private double volume = 0.5;
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private Song currentSongInPlaylist;
    private Playlist currentPlaylist;
    private MainController mainController;

    private ObjectProperty<String> currentSongTitleProperty = new SimpleObjectProperty<>();
    private List<Song> playlistSongs;

    public List<Song> getAllSongs() throws DBException {
        return dalManager.getAllSongs();
    }

    public List<Song> getFilteredSongs(String filter) throws DBException {
        return dalManager.getFilteredSongs(filter);
    }

    public List<Playlist> getAllPlaylists() throws DBException {
        return dalManager.getAllPlaylists();
    }

    public List<Song> getSongsOnPlaylist(int playlistId) throws DBException {
        return dalManager.getSongsOnPlaylist(playlistId);
    }

    public Song getCurrentSong() throws DBException {
        return currentSong;
    }

    public Song getCurrentSongInPlaylist(Playlist playlist) throws DBException {
        return currentSongInPlaylist;
    }


    public String getCurrentSongTitle() throws DBException {
        return currentSong.getTitle();
    }


    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public Song findNextSong(List<Song> songList, Song currentSong) {
        if (currentSong == null) {
            throw new IllegalArgumentException("Current song is null.");
        }

        int currentIndex = songList.indexOf(currentSong);

        if (currentIndex != -1 && currentIndex < songList.size() - 1) {
            return songList.get(currentIndex + 1);
        }

        return null;
    }

    public Song findNextSongInPlaylist(Playlist playlist, Song currentSong) {
        if (currentSong == null && playlist == null) {
            throw new IllegalArgumentException("Current song is null.");
        }

        List<Song> playlistSongs = mainController.getSongsInPlaylist();

        int currentIndex = playlistSongs.indexOf(currentSong);

        if (currentIndex != -1 && currentIndex < playlistSongs.size() - 1) {
            return playlistSongs.get(currentIndex + 1);
        }

        return null;
    }

    public Song findPreviousSong(List<Song> songList, Song currentSong) {
        if (currentSong == null) {
            throw new IllegalArgumentException("Current song is null.");
        }

        int currentIndex = songList.indexOf(currentSong);

        if (currentIndex > 0) {
            return songList.get(currentIndex - 1);
        }

        return null;
    }

    public Playlist findPreviousSongInPlaylist(List<Playlist> playlistList, Song currentSong) {
        if (currentSong == null) {
            throw new IllegalArgumentException("Current song is null.");
        }

        int currentIndex = playlistList.indexOf(currentSong);

        if (currentIndex > 0) {
            return playlistList.get(currentIndex - 1);
        }

        return null;
    }

    public void setCurrentSongTitle(String title) {
        this.currentSongTitleProperty.set(title);
    }

    public ObjectProperty<String> currentSongTitleProperty() {
        return currentSongTitleProperty;
    }

    public void playSong(Song song) throws Exception {
        if (song == null) {
            throw new IllegalArgumentException("No song provided.");
        }

        String filePath = song.getFilePath();
        Path path = Paths.get(filePath);

        if (mediaPlayer != null && Files.exists(path)) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                mediaPlayer.play();
                return;
            } else {
                mediaPlayer.stop();
                //mediaPlayer.dispose();
            }
        }

        currentSong = song;
        setCurrentSongTitle("Playing: " + song.getTitle());


        if (!Files.exists(path)) {
            throw new DBException("Song does not exist.");
        } else {
            Media media = new Media(new File(filePath).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volume);
            mediaPlayer.play();

            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
            });
        }
    }

    public void playSongInPlaylist(Song song, Playlist playlist) throws Exception {
        if (song == null && playlist == null) {
            throw new IllegalArgumentException("Song or Playlist cannot be null");
        }

        List<Song> playlistSongs = new ArrayList<>(getSongsOnPlaylist(playlist.getId()));


        String filePath = song.getFilePath();
        Path path = Paths.get(filePath);

        if (mediaPlayer != null && Files.exists(path)) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                mediaPlayer.play();
                return;
            } else {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
        }

        currentSongInPlaylist = song;

        if (!Files.exists(path)) {
            throw new Exception("No path found");
        }
        else {
            Media media = new Media(new File(filePath).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volume);
            mediaPlayer.play();

            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Song finished playing.");
                mediaPlayer.stop();
            });
        }
    }




    public void stopSong() throws Exception {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        }
    }


    public void setVolume(double volume) {
        if(volume < 0.0 || volume > 100.0) {
            throw new IllegalArgumentException("Volume must be between 0.0 and 1.0");
        }
        this.volume = volume;
        mediaPlayer.setVolume(volume);
    }
    public double getVolume() {
        return volume;
    }



    public String openFile(Window window) {
        ChooseFile fileBrowser = new ChooseFile(window);
        return fileBrowser.getSelectedFilePath();
    }
    public boolean editPlaylistName(Playlist playlist) {
        return dalManager.editPlaylistName(playlist);
    }
    public boolean editSong(Song song) {
        return dalManager.editSong(song);
    }
    public Category returnCategoryName(int id) {
        return dalManager.getOneCategory(id);
    }
    public List<Category> getAllCategories() {
        return dalManager.getAllCategories();
    }

    public Playlist addPlaylist(Playlist playlist) {
        return dalManager.createPlaylist(playlist);
    }

    public void deletePlaylist(Playlist playlist) {
        dalManager.deletePlaylist(playlist);
    }
    public void moveSongUp(Song song, int playlist, boolean up) {
            dalManager.moveSongUp(song, playlist, up);
    }
    public int numberOfSongsInList(int playlistId) {
        return dalManager.getNumberOfSongsInList(playlistId);
    }
    public void deleteFromPlaylist(Song selectedSongInPlaylist, Playlist selectedPlaylist) {
        dalManager.deleteFromPlaylist(selectedSongInPlaylist, selectedPlaylist);
    }
    public boolean deleteSong(Song selectedSong, boolean deleteFile) {
        if (dalManager.deleteSong(selectedSong)) {
            if (deleteFile) {
                FileManager fileManager = new FileManager();
                if (fileManager.deleteFile(selectedSong.getFilePath()))
                    return true;
                else
                    throw new RuntimeException("File could not be deleted");
            }
            return true;
        } else
            throw new RuntimeException("Song could not be deleted from database");
    }

    public void addSongToPlaylist(int playlistId, int songId) {
        dalManager.addSongToPlaylist(playlistId, songId);
    }

    public int addSong(String title, String artist, String filePath, Time duration, int category) {
        return dalManager.addNewSong (title, artist, duration, filePath, category);
    }

    public int createNewCategory(String name) {
        return dalManager.createNewCategory(name);
    }

    public int getNumberOfSongsInPLaylist(int playlistId) {
        return dalManager.getNumberOfSongsInPlaylist(playlistId);
    }

    public void updatePlaylistTime(Time totalTime, int id) {
        dalManager.updatePlaylistTime (totalTime, id);
    }
    public void updateTotalNumberOfSongs(int count, int id) {
        dalManager.updateNumberOfSongs(count, id);
    }
}
