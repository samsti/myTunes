package dk.easv.mytunes.ui;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import dk.easv.mytunes.exceptions.DBException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Time;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    @FXML private StackPane rootPane;
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
    @FXML private TextField txtNewPlaylist;
    @FXML private VBox popupVBox;
    @FXML private VBox popupBg;
    @FXML private VBox popupNewSong;
    @FXML private TextField txtSongTitle;
    @FXML private TextField txtSongArtist;
    @FXML private ChoiceBox<String> choiceCategory;
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
    private final static int DELETING_PLAYLIST = 0;
    private final static int DELETING_SONG_FROM_PLAYLIST = 1;
    private final static int DELETING_SONG = 2;
    private final static int DELETING_INVALID = -1;
    private final static String DELETING_DEFAULT_TEXT = "Are you sure you want to delete ";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        popupBg.setPrefWidth(rootPane.getWidth());
        popupBg.setPrefHeight(rootPane.getHeight());
        loadSongs();

//        // Adds a listener to any slider changes
//        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                double volume = newValue.doubleValue();
//                bllManager.setVolume(volume); //Passes the new volume to the Business Layer
//            }
//        });
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
        popupBg.setVisible(true);
        popupVBox.setVisible(true);
        popupVBox.toFront();

    }

    @FXML
    private void cancelButtonClicked(ActionEvent event) {
        txtNewPlaylist.setText("");
        popupBg.setVisible(false);
        popupVBox.setVisible(false);

    }
    @FXML
    private void saveButtonClicked(ActionEvent event) {
        txtNewPlaylist.setText("");
        popupBg.setVisible(false);
        popupVBox.setVisible(false);
        //TODO: save to DB
    }
    @FXML
    private void btnNewSongClicked(ActionEvent event) {
        popupBg.setVisible(true);
        popupNewSong.setVisible(true);
        popupNewSong.toFront();
    }
    @FXML
    private void btnChooseCategoryClicked(ActionEvent event) {
        btnChoose.setVisible(false);
        txtNewCategory.setVisible(true);
        btnChooseCategory.setVisible(false);
        btnAddCategory.setVisible(true);
    }
    @FXML
    private void btnCancelSongClicked(ActionEvent event) {
        txtFilePath.setText("");
        txtSongTitle.setText("");
        txtSongArtist.setText("");
        txtTime.setText("");
        popupBg.setVisible(false);
        popupNewSong.setVisible(false);
        txtNewCategory.setVisible(false);
        btnAddCategory.setVisible(false);
        btnChooseCategory.setVisible(true);
    }
    @FXML
    private void btnDeletePlayListClicked (ActionEvent event) {
        if (tblPlaylist.getSelectionModel().getSelectedItem() != null) {
            popupBg.setVisible(true);
            popupDelete.setVisible(true);
            cbDeleteFile.setVisible(false);
            Playlist playlistToDelete = tblPlaylist.getSelectionModel().getSelectedItem();
            lblDeleting.setText(DELETING_DEFAULT_TEXT + playlistToDelete.getName() + " playlist?");
        }
    }
    @FXML
    private void btnDeleteFromPlaylistClicked(ActionEvent event) {
        if (lstSongsInPlaylist.getSelectionModel().getSelectedItem() != null) {
            popupBg.setVisible(true);
            popupDelete.setVisible(true);
            cbDeleteFile.setVisible(false);
            Playlist playlistToDeleteFrom = tblPlaylist.getSelectionModel().getSelectedItem();
            //SongsInPlaylist songToDelete = lstSongsInPlaylist.getSelectionModel().getSelectedItem();
            lblDeleting.setText(DELETING_DEFAULT_TEXT
                    + "\nfrom playlist: " + playlistToDeleteFrom.getName() + "?");
        }
    }
    @FXML
    private void btnDeleteSongClicked(ActionEvent event) {
        if (tblSongs.getSelectionModel().getSelectedItem() != null) {
            popupBg.setVisible(true);
            popupDelete.setVisible(true);
            cbDeleteFile.setVisible(true);
            Song songToDelete = tblSongs.getSelectionModel().getSelectedItem();
            lblDeleting.setText(DELETING_DEFAULT_TEXT + songToDelete.getTitle() + " song?");
        }
    }

    @FXML
    private void btncancDeleteClicked(ActionEvent event)  {
        cbDeleteFile.setSelected(false);
        popupBg.setVisible(false);
        popupDelete.setVisible(false);
    }
    @FXML
    private void btnYesDeleteClicked(ActionEvent event) {
        //TODO: delete from database
    }

    @FXML
    private void btnCloseClicked(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }


}