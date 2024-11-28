package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Category;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.exceptions.DBException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DALManager {

    private final ConnectionManager cm;

    public DALManager() {
        try {
            cm = new ConnectionManager();
        } catch (DBException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList();
        try (Connection con = cm.getConnection()) {
            String sqlcommandSelect = "SELECT * FROM songs";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            ResultSet rs = pstmtSelect.executeQuery();
            while(rs.next())
            {
                songs.add(new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getTime("duration"),
                        rs.getString("file_path"),
                        rs.getInt("category"))
                );
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return songs;
    }

    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = new ArrayList();
        try (Connection con = cm.getConnection()) {
            String sqlcommandSelect = "SELECT * FROM playlists";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            ResultSet rs = pstmtSelect.executeQuery();
            while(rs.next())
            {
                playlists.add(new Playlist(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getTime("total_duration"),
                        rs.getInt("number_of_songs"))
                );
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return playlists;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList();
        try (Connection con = cm.getConnection()) {
            String sqlcommandSelect = "SELECT * FROM category";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            ResultSet rs = pstmtSelect.executeQuery();
            while(rs.next())
            {
                categories.add(new Category(
                        rs.getInt("id"),
                        rs.getString("category"))
                );
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return categories;
    }

    public List<Song> getSongsOnPlaylist(int playlistId) {
        List<Song> songsOnPlaylist = new ArrayList();
        try (Connection con = cm.getConnection()) {
            String sqlcommandSelect = "SELECT s.id, s.title, s.artist, s.duration, s.file_path, s.category FROM songs_in_playlist sip JOIN songs s ON sip.songId = s.id WHERE sip.playlistId = ? ORDER BY sip.[order];";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            pstmtSelect.setInt(1, playlistId);
            ResultSet rs = pstmtSelect.executeQuery();
            while(rs.next())
            {
                songsOnPlaylist.add(new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getTime("duration"),
                        rs.getString("file_path"),
                        rs.getInt("category"))
                );
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return songsOnPlaylist;
    }

    public Playlist createPlaylist(Playlist playlist) {
        try (Connection con = cm.getConnection()) {
            String sqlcommandInsert = "INSERT INTO playlists (name) VALUES (?)";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandInsert);
            pstmtSelect.setString(1, playlist.getName());
            pstmtSelect.execute();
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return playlist;
    }
}
