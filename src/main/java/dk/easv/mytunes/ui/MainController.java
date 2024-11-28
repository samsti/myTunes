package dk.easv.mytunes.ui;

import dk.easv.mytunes.be.Category;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.bll.BLLManager;
import dk.easv.mytunes.dal.ChooseFile;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import dk.easv.mytunes.exceptions.DBException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.net.URL;
import java.sql.Time;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    @FXML private StackPane rootPane;
    @FXML private Button btnPlay;
    @FXML private Button btnNewPlayList;
    @FXML private Button btnEditPlayList;
    @FXML private Button btnDeletePlayList;
    @FXML private Button btnAddSongToPlayList;
    @FXML private Button btnMoveSongUp;
    @FXML private Button btnMoveSongDown;
    @FXML private Button btnDeleteFromPlaylist;
    @FXML private Button btnNewSong;
    @FXML private Button btnEditSong;
    @FXML private Button btnDeleteSong;
    @FXML private Button btnClose;
    @FXML private TextField txtFilter;
    @FXML private TextField txtNewPlaylist;
    @FXML private VBox popupVBox;
    @FXML private VBox popupBg;
    @FXML private VBox popupNewSong;
    @FXML private TextField txtSongTitle;
    @FXML private TextField txtSongArtist;
    @FXML private ChoiceBox<Category> choiceCategory;
    @FXML private Button btnChooseCategory;
    @FXML private Button btnAddCategory;
    @FXML private TextField txtTime;
    @FXML private TextField txtFilePath;
    @FXML private Button btnChoose;
    @FXML private Button btnSaveSong;
    @FXML private Button btnCancelSong;
    @FXML private TextField txtNewCategory;
    @FXML private TableView<Playlist> tblPlaylist;
    @FXML private TableColumn<Playlist, String> columnName;
    @FXML private TableColumn<Playlist, Integer> columnItem;
    @FXML private TableColumn<Playlist, Time> columnTotalDuration;
    @FXML private ListView<Song> lstSongsInPlaylist;
    @FXML private TableView<Song> tblSongs;
    @FXML private TableColumn<Song, String> titleColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, String> durationColumn;
    @FXML private TableColumn<Song, String> categoryColumn;
    @FXML private Slider volumeSlider;
    private final MyTunesModel model = new MyTunesModel();
    @FXML private VBox popupDelete;
    @FXML private Label lblDeleting;
    @FXML private CheckBox cbDeleteFile;
    @FXML private Button btncancDelete;
    @FXML private Button btnYesDelete;
    @FXML private Slider sldVolume;
    private final static int DELETING_PLAYLIST = 0;
    private final static int DELETING_SONG_FROM_PLAYLIST = 1;
    private final static int DELETING_SONG = 2;
    private final static int DELETING_INVALID = -1;
    private final static String DELETING_DEFAULT_TEXT = "Are you sure you want to delete ";
    private BLLManager manager;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        manager = new BLLManager();
        popupBg.setPrefWidth(rootPane.getWidth());
        popupBg.setPrefHeight(rootPane.getHeight());
        loadSongs();

//        // Adds a listener to any slider changes
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double volume = newValue.doubleValue();
                manager.setVolume(volume/100); //Passes the new volume to the Business Layer
            }
        });
    }

    private void loadSongs() {
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnTotalDuration.setCellValueFactory(new PropertyValueFactory<>("totalDuration"));
        columnItem.setCellValueFactory(new PropertyValueFactory<>("numberOfSongs"));

        // Load songs into TableView
        try {
            model.loadSongs();
            tblSongs.setItems(model.getSongs());
            model.loadPlaylists();
            tblPlaylist.setItems(model.getPlaylists());

            tblPlaylist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    try {
                        model.loadSongsOnPlaylist(newValue.getId());
                        lstSongsInPlaylist.setItems(model.getSongsOnPlaylist());
                    } catch (DBException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (DBException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void newPlaylistClicked(ActionEvent event) {
        openPlaylistPopUp();

    }

    @FXML
    private void cancelButtonClicked(ActionEvent event) {
        closePlaylistPopUp();

    }
    @FXML
    private void saveButtonClicked(ActionEvent event) {
        Playlist editPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();
        editPlaylist.setName(txtNewPlaylist.getText().trim());
        if (manager.editPlaylistName(editPlaylist)) {
            tblPlaylist.getSelectionModel().getSelectedItem().setName(txtNewPlaylist.getText().trim());
            tblPlaylist.refresh();
        }
        closePlaylistPopUp();
    }
    @FXML
    private void btnNewSongClicked(ActionEvent event) {
        openSongsPopUp();
    }
    @FXML
    private void btnChooseCategoryClicked(ActionEvent event) {
        btnChoose.setVisible(false);
        txtNewCategory.setVisible(true);
        btnChoose.setVisible(false);
        btnAddCategory.setVisible(true);
    }
    @FXML
    private void btnCancelSongClicked(ActionEvent event) {
        closeSongsPopUp();
    }
    @FXML
    private void btnDeletePlayListClicked (ActionEvent event) {
        if (tblPlaylist.getSelectionModel().getSelectedItem() != null) {
            openDeleteWindow();
            Playlist playlistToDelete = tblPlaylist.getSelectionModel().getSelectedItem();
            lblDeleting.setText(DELETING_DEFAULT_TEXT + playlistToDelete.getName() + " playlist?");
        }
    }
    @FXML
    private void btnDeleteFromPlaylistClicked(ActionEvent event) {
        if (lstSongsInPlaylist.getSelectionModel().getSelectedItem() != null) {
            openDeleteWindow();
            Playlist playlistToDeleteFrom = tblPlaylist.getSelectionModel().getSelectedItem();
            Song songToDelete = lstSongsInPlaylist.getSelectionModel().getSelectedItem();
            lblDeleting.setText(DELETING_DEFAULT_TEXT + songToDelete.getTitle()
                    + "\nfrom playlist: " + playlistToDeleteFrom.getName() + "?");
        }
    }
    @FXML
    private void btnDeleteSongClicked(ActionEvent event) {
        if (tblSongs.getSelectionModel().getSelectedItem() != null) {
            openDeleteWindow();
            cbDeleteFile.setVisible(true);
            Song songToDelete = tblSongs.getSelectionModel().getSelectedItem();
            lblDeleting.setText(DELETING_DEFAULT_TEXT + songToDelete.getTitle() + " song?");
        }
    }

    @FXML
    private void btncancDeleteClicked(ActionEvent event)  {
        closeDeleteWindow();
    }
    @FXML
    private void btnYesDeleteClicked(ActionEvent event) {
        //TODO: delete from database
        closeDeleteWindow();
    }

    @FXML
    private void btnCloseClicked(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void searchSongs(ActionEvent event) {

    }
    @FXML
    private void btnPlayClicked(ActionEvent event) {
        //manager.playSong(lstSongsInPlaylist.getSelectionModel().getSelectedItem());
        manager.playSong(new Song (1, "Silent night", "YT", Time.valueOf("00:02:22"),"C:\\Users\\ervin\\Documents\\School\\SCO1\\Project\\r\\myTunes\\src\\main\\resources\\music\\Silent.mp3",1));
        if (manager.isPlaying())
            btnPlay.setText("Pause");
        else
            btnPlay.setText("Play");
    }
    @FXML
    private void btnChooseClicked(ActionEvent event) {
        String filepath =  manager.openFile(btnChoose.getScene().getWindow());
        txtFilePath.setText(filepath);
    }

    @FXML
    private void btnEditPlayListClicked(ActionEvent event) {
        Playlist playlistToEdit = tblPlaylist.getSelectionModel().getSelectedItem();
        if (playlistToEdit != null) {
            openPlaylistPopUp();
            txtNewPlaylist.setText(playlistToEdit.getName());
        }
    }
    @FXML
    private void btnEditSongClicked(ActionEvent event) {
        Song songToEdit = tblSongs.getSelectionModel().getSelectedItem();
        System.out.println(songToEdit);
        if (songToEdit != null) {
            openSongsPopUp();
            txtSongTitle.setText(songToEdit.getTitle());
            txtSongArtist.setText(songToEdit.getArtist());
            txtFilePath.setText(songToEdit.getFilePath());
            txtTime.setText(songToEdit.getDuration());
            choiceCategory.setValue(manager.returnCategoryName(songToEdit.getCategory()));
        }
    }
    @FXML
    private void btnSaveSongClicked(ActionEvent event) {
        Song songToEdit = tblSongs.getSelectionModel().getSelectedItem();
        if (songToEdit != null) {
            songToEdit.setTitle(txtSongTitle.getText().trim());
            songToEdit.setArtist(txtSongArtist.getText().trim());
            songToEdit.setFilePath(txtFilePath.getText().trim());
            songToEdit.setDuration(txtTime.getText().trim());
            songToEdit.setCategory(choiceCategory.getSelectionModel().getSelectedItem().getId());
            if (manager.editSong(songToEdit))
                closeSongsPopUp();
        }
        else
            throw new RuntimeException("No song selected");
    }


    /**
     *
     *   Opening and closing the popup windows
     *
     */
    private void openPlaylistPopUp() {
        popupBg.setVisible(true);
        popupVBox.setVisible(true);
        txtNewPlaylist.setText("");
    }

    private void closePlaylistPopUp() {
        popupBg.setVisible(false);
        popupVBox.setVisible(false);
    }
    private void openSongsPopUp() {
        popupBg.setVisible(true);
        popupNewSong.setVisible(true);
        popupNewSong.toFront();
        txtFilePath.setText("");
        txtSongTitle.setText("");
        txtSongArtist.setText("");
        txtTime.setText("");
        ObservableList<Category> categories = FXCollections.observableArrayList();
        categories.addAll(model.getCategories());
        choiceCategory.setItems(categories);
    }
    private void closeSongsPopUp() {

        popupBg.setVisible(false);
        popupNewSong.setVisible(false);
        txtNewCategory.setVisible(false);
        btnAddCategory.setVisible(false);
        btnChooseCategory.setVisible(true);
    }
    private void openDeleteWindow() {
        popupBg.setVisible(true);
        popupDelete.setVisible(true);
        cbDeleteFile.setVisible(false);
    }
    private void closeDeleteWindow() {
        cbDeleteFile.setSelected(false);
        popupBg.setVisible(false);
        popupDelete.setVisible(false);
    }
}