package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Category;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.exceptions.DBException;
import javafx.scene.control.Alert;

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
            while (rs.next()) {
                songs.add(new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getTime("duration"),
                        rs.getString("file_path"),
                        rs.getInt("category"))
                );
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return songs;
    }

    public List<Song> getFilteredSongs(String filter) {
        List<Song> filteredSongs = new ArrayList();
        try (Connection con = cm.getConnection()) {
            String sqlcommandSelect = "SELECT * FROM songs WHERE title LIKE ? OR artist LIKE ?;";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            pstmtSelect.setString(1, "%" + filter + "%");
            pstmtSelect.setString(2, "%" + filter + "%");
            ResultSet rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                filteredSongs.add(new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getTime("duration"),
                        rs.getString("file_path"),
                        rs.getInt("category"))
                );
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return filteredSongs;
    }

    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = new ArrayList();
        try (Connection con = cm.getConnection()) {
            String sqlcommandSelect = "SELECT * FROM playlists";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            ResultSet rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                playlists.add(new Playlist(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getTime("total_duration"),
                        rs.getInt("number_of_songs"))
                );
            }
        } catch (SQLException ex) {
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
            while (rs.next()) {
                categories.add(new Category(
                        rs.getInt("id"),
                        rs.getString("category"))
                );
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return categories;
    }

    public Category getOneCategory(int id) {
        try (Connection con = cm.getConnection()) {
            Category category;
            String sqlcommandSelect = "SELECT * FROM category where id = ?";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            pstmtSelect.setInt(1, id);
            ResultSet rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                category = new Category(rs.getInt("id"), rs.getString("category"));
                return category;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        throw new RuntimeException("No Category found with id " + id);
    }

    public List<Song> getSongsOnPlaylist(int playlistId) {
        List<Song> songsOnPlaylist = new ArrayList();
        try (Connection con = cm.getConnection()) {
            String sqlcommandSelect = "SELECT s.id, s.title, s.artist, s.duration, s.file_path, s.category, sip.[order] FROM songs_in_playlist sip JOIN songs s ON sip.songId = s.id WHERE sip.playlistId = ? ORDER BY sip.[order];";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            pstmtSelect.setInt(1, playlistId);
            ResultSet rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                Song toAdd = new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getTime("duration"),
                        rs.getString("file_path"),
                        rs.getInt("category"));
                toAdd.setOrder(rs.getInt("order"));
                songsOnPlaylist.add(toAdd);
            }
        } catch (SQLException ex) {
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
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return playlist;
    }

    public void addSongToPlaylist(int playlistId, int songId) {
        try (Connection con = cm.getConnection()) {
            String sqlCheck = "SELECT * FROM songs_in_playlist WHERE playlistId = ? AND songId = ?";
            PreparedStatement pstmtCheck = con.prepareStatement(sqlCheck);
            pstmtCheck.setInt(1, playlistId);
            pstmtCheck.setInt(2, songId);
            ResultSet rs = pstmtCheck.executeQuery();

            if (rs.next()) {
                showAlert("Duplicate Entry", "This song is already in the playlist!");
                return;
            }

            String sqlInsert = "INSERT INTO songs_in_playlist (playlistId, songId) VALUES (?, ?)";
            PreparedStatement pstmtInsert = con.prepareStatement(sqlInsert);
            pstmtInsert.setInt(1, playlistId);
            pstmtInsert.setInt(2, songId);
            pstmtInsert.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void deletePlaylist(Playlist playlist) {
        try (Connection con = cm.getConnection()) {
            String sqlcommandInsert = "DELETE FROM songs_in_playlist WHERE playlistId = ?";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandInsert);
            pstmtSelect.setInt(1, playlist.getId());
            pstmtSelect.execute();
            sqlcommandInsert = "DELETE FROM playlists WHERE id = ?";
            pstmtSelect = con.prepareStatement(sqlcommandInsert);
            pstmtSelect.setInt(1, playlist.getId());
            pstmtSelect.execute();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean editPlaylistName(Playlist playlist) {
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

    public boolean deleteFromPlaylist(Song song, Playlist playlist) {
        try (Connection con = cm.getConnection()) {
            String sqlcommandInsert = "DELETE FROM songs_in_playlist WHERE songId = ? AND playlistId = ? AND [order] = ?";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandInsert);
            pstmtSelect.setInt(1, song.getId());
            pstmtSelect.setInt(2, playlist.getId());
            pstmtSelect.setInt(3, song.getOrder());
            pstmtSelect.execute();
            return true;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
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
    public boolean deleteSong(Song song) {
        try (Connection con = cm.getConnection()) {
            String sqlcommandInsert = "DELETE FROM songs_in_playlist WHERE songId = ?";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandInsert);
            pstmtSelect.setInt(1, song.getId());
            pstmtSelect.execute();
            sqlcommandInsert = "DELETE FROM songs WHERE id = ?";
            pstmtSelect = con.prepareStatement(sqlcommandInsert);
            pstmtSelect.setInt(1, song.getId());
            pstmtSelect.execute();
            return true;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void moveSongUp(Song song, int playlistId, boolean up) {
        try (Connection con = cm.getConnection()) {
            int newOrder = 0;
            int swappingId = 0;

            //Gets the id of the song where it needs to swap checking the next or the previous order
            String sqlcommand = "SELECT * FROM songs_in_playlist WHERE playlistId = ? AND [order] " +
                    (up ? "< " : "> ") + "? ORDER BY [order] " +
                    (up ? "DESC" : "ASC") + " OFFSET 0 ROWS FETCH NEXT 1 ROW ONLY";
            PreparedStatement statement = con.prepareStatement(sqlcommand);
            statement.setInt(1, playlistId);
            statement.setInt(2, song.getOrder());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                swappingId = rs.getInt("id");
                newOrder = rs.getInt("order");
            }

            //Update the song which is getting moved
            sqlcommand = "UPDATE songs_in_playlist SET [order] = ? WHERE songId = ? AND playlistId = ?";
            statement = con.prepareStatement(sqlcommand);
            statement.setInt(1, newOrder);
            statement.setInt(2, song.getId());
            statement.setInt(3, playlistId);
            statement.executeUpdate();

            //Update the song in which's place it got moved to
            sqlcommand = "UPDATE songs_in_playlist SET [order] = ? WHERE id = ?";
            statement = con.prepareStatement(sqlcommand);
            statement.setInt(1, song.getOrder());
            statement.setInt(2, swappingId);
            statement.executeUpdate();
            statement.close();
            //return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getNumberOfSongsInList(int playlistId) {
        try (Connection con = cm.getConnection()) {
            String sqlcommand = "SELECT COUNT(*) AS TotalSongs FROM songs_in_playlist WHERE playlistId = ?;";
            PreparedStatement statement = con.prepareStatement(sqlcommand);
            statement.setInt(1, playlistId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("TotalSongs"); // Return the count
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

}
