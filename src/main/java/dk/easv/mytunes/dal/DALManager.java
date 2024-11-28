package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Category;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.exceptions.DBException;

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

    public Category getOneCategory (int id) {
        try (Connection con = cm.getConnection()) {
            Category category;
            String sqlcommandSelect = "SELECT * FROM category where id = ?";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            pstmtSelect.setInt(1, id);
            ResultSet rs = pstmtSelect.executeQuery();
            while(rs.next())
            {
                category = new Category(rs.getInt("id"), rs.getString("category"));
                return category;
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        throw new RuntimeException("No Category found with id " + id);
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

    public void deletePlaylist(Playlist playlist) {
        try (Connection con = cm.getConnection()) {
            String sqlcommandInsert = "DELETE FROM playlists WHERE id = ?";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandInsert);
            pstmtSelect.setInt(1, playlist.getId());
            pstmtSelect.execute();
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    public boolean editPlaylistName (Playlist playlist) {
        try (Connection con = cm.getConnection()) {
            String sqlcommand = "UPDATE playlists SET name = ? WHERE id = ?";
            PreparedStatement statement = con.prepareStatement(sqlcommand);
            statement.setString(1, playlist.getName());
            statement.setInt(2, playlist.getId());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean editSong(Song song) {
        try (Connection con = cm.getConnection()) {
            String sqlcommand = "UPDATE songs SET title = ?, artist = ?, duration = ?, file_path = ?, category = ? WHERE id = ?";
            PreparedStatement statement = con.prepareStatement(sqlcommand);
            statement.setString(1, song.getTitle());
            statement.setString(2, song.getArtist());
            statement.setString(3, song.getDuration());
            statement.setString(4, song.getFilePath());
            statement.setInt(5, song.getCategory());
            statement.setInt(6, song.getId());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
