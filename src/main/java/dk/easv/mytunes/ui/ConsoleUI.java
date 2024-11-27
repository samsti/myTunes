package dk.easv.mytunes.ui;

import dk.easv.mytunes.be.Category;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.DALManager;
import dk.easv.mytunes.exceptions.DBException;

import java.io.IOException;
import java.util.List;

public class ConsoleUI {

    private final DALManager dalManager;

    public ConsoleUI() {
        dalManager = new DALManager();
    }

    public void start() {
        try {
            displayDB();
        } catch (DBException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void displayDB() throws DBException{
        List<Song> songs = dalManager.getAllSongs();
        List<Playlist> playlists = dalManager.getAllPlaylists();
        List<Category> categories = dalManager.getAllCategories();


        System.out.println("---- PLAYLISTS ----");
        for(Playlist playlist : playlists) {

            System.out.println("ID: " + playlist.getId() + " - Name: " + playlist.getName() + " - Total duration: " + playlist.getTotalDuration() + " - Number of songs: " + playlist.getNumberOfSongs());
        }

        System.out.println("---- CATEGORIES ----");
        for(Category category : categories) {
            System.out.println("ID: " + category.getId() + " - Category: " + category.getCategory() + "");
        }
    }

    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();
        ui.start();
    }
}
