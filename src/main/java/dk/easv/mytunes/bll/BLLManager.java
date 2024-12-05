package dk.easv.mytunes.bll;

import dk.easv.mytunes.be.Category;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.ChooseFile;
import dk.easv.mytunes.dal.DALManager;
import dk.easv.mytunes.dal.FileManager;
import dk.easv.mytunes.exceptions.DBException;
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
    private ObjectProperty<String> currentSongTitleProperty = new SimpleObjectProperty<>();
    private Playlist currentPlaylist;
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

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public Song findNextSong(List<Song> songList, Song currentSong) {
        if (currentSong == null) {
            throw new IllegalArgumentException("Current song is null.");
        }
        int currentIndex = -1;
        if (!songList.equals(getAllSongs())) {
            currentIndex = currentSong.getOrder() - 1;
        }
        else  {
            for (int i = 0; i < songList.size(); i++) {
                System.out.println("Comparing song ID: " + songList.get(i).getId() + " with current song ID: " + currentSong.getId());
                if (songList.get(i).getId() == currentSong.getId()) {
                    System.out.println("BLL found the ID: " + songList.get(i).getTitle());
                    currentIndex = i;
                    break;
                }
            }
        }
        if (currentIndex != -1 && currentIndex < songList.size() - 1) {
            return songList.get(currentIndex + 1);
        }

        return null;
    }

    public Song findPreviousSong(List<Song> songList, Song currentSong) {
        if (this.currentSongInPlaylist == null) {
            throw new IllegalArgumentException("Current song is null.");
        }

        int currentIndex = currentSongInPlaylist.getOrder()-1;

        if (currentIndex > 0) {
            return songList.get(currentIndex - 1);
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
        playlistSongs = new ArrayList<>(getAllSongs());
        if (song == null && playlist == null) {
            //throw new IllegalArgumentException("Song or Playlist cannot be null");
            currentSongInPlaylist = playlistSongs.getFirst();
        }
        if (playlist != null) {
            playlistSongs = new ArrayList<>(getSongsOnPlaylist(playlist.getId()));
            currentSongInPlaylist = playlistSongs.getFirst();
        }
        if (song != null)
            currentSongInPlaylist = song;
        boolean songFound = playlistSongs.stream().anyMatch(playlistSong -> playlistSong.getId() == currentSongInPlaylist.getId());
        if (!songFound) {
            throw new IllegalArgumentException("The song is not in the provided playlist.");
        }


        playSong(currentSongInPlaylist);


        currentPlaylist = playlist;

        List<Song> finalPlaylistSongs = playlistSongs;
        mediaPlayer.setOnEndOfMedia(() -> {
            try {
                /** Find the next song in the playlist
                 int currentIndex = finalPlaylistSongs.indexOf(finalSong);
                 int nextIndex = currentIndex + 1;

                 if (nextIndex < finalPlaylistSongs.size()) {
                 Song nextSong = finalPlaylistSongs.get(nextIndex);
                 playSongInPlaylist(nextSong, playlist);
                 } else {
                 System.out.println("Reached the end of the playlist.");
                 }*/
                Song nextSong = findNextSong(finalPlaylistSongs, currentSongInPlaylist);
                playSongInPlaylist(nextSong, currentPlaylist);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

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

    public String getCurrentSongTitle() throws DBException {
        return currentSong.getTitle();
    }

    public Song getCurrentSongInPlaylist(Playlist playlist) throws DBException {
        return currentSongInPlaylist;
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
