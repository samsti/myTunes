package dk.easv.mytunes.bll;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.ChooseFile;
import dk.easv.mytunes.dal.DALManager;
import dk.easv.mytunes.exceptions.DBException;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Window;

import java.io.File;
import java.util.List;

public class BLLManager {
    //private double volume;
    private final DALManager dalManager = new DALManager();
    private double volume;
    private MediaPlayer mediaPlayer;

    public List<Song> getAllSongs() throws DBException {
        return dalManager.getAllSongs();
    }

    public List<Playlist> getAllPlaylists() throws DBException {
        return dalManager.getAllPlaylists();
    }

    public List<Song> getSongsOnPlaylist(int playlistId) throws DBException {
        return dalManager.getSongsOnPlaylist(playlistId);
    }

    public void playSong (Song song) throws DBException {
        String filePath = song.getFilePath();
        //System.out.println(filePath);

        // Create a Media object
        Media media = new Media(new File(filePath).toURI().toString());

        // Create a MediaPlayer object
        mediaPlayer = new MediaPlayer(media);
        volume = 0.2;
        // Play the song
        mediaPlayer.setVolume(volume);
        mediaPlayer.play();

        mediaPlayer.setOnEndOfMedia(() -> {
            System.out.println("Song finished playing.");
            mediaPlayer.stop(); // Stops the player when finished
        });
    }

    public void setVolume(double volume) {
        if(volume < 0.0 || volume > 1.0) {
            throw new IllegalArgumentException("Volume must be between 0.0 and 1.0");
        }
        this.volume = volume;
        mediaPlayer.setVolume(volume);
    }
    public double getVolume(double v) {
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
}
