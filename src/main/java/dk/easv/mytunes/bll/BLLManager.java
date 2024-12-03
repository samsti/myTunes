package dk.easv.mytunes.bll;

import dk.easv.mytunes.be.Category;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.ChooseFile;
import dk.easv.mytunes.dal.DALManager;
import dk.easv.mytunes.dal.FileManager;
import dk.easv.mytunes.exceptions.DBException;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;



public class BLLManager {
    //private double volume;
    private final DALManager dalManager = new DALManager();
    private double volume = 0.5;
    private MediaPlayer mediaPlayer;
    private Song currentSong;


    public List<Song> getAllSongs() throws DBException {
        return dalManager.getAllSongs();
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
                mediaPlayer.dispose();
            }
        }

        currentSong = song;

        if (!Files.exists(path)) {
            throw new Exception("Path to song does not exist on your PC.");
        } else {
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


    public void playPreviousSong() throws Exception {

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

    public boolean isPlaying() {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            System.out.println("playing");
            return true;
        }
        return false;
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
    public boolean deleteFromPlaylist(Song selectedSongInPlaylist, Playlist selectedPlaylist) {
        return dalManager.deleteFromPlaylist(selectedSongInPlaylist, selectedPlaylist);
    }
    public boolean deletSong(Song selectedSong, boolean deleteFile) {
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
}
