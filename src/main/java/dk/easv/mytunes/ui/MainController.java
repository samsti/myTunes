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
    @FXML private ListView<Playlist> lstPlaylist;
    @FXML private ListView<Song> lstSongsInPlaylist;
    @FXML private TableView<Song> tblSongs;
    @FXML private TableColumn<Song, String> titleColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, String> durationColumn;
    @FXML private TableColumn<Song, String> categoryColumn;
    @FXML private Slider volumeSlider;
    private final MyTunesModel model = new MyTunesModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        popupBg.setPrefWidth(rootPane.getWidth());
        popupBg.setPrefHeight(rootPane.getHeight());
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());

        // Load songs into TableView
        try {
            model.loadSongs();
            tblSongs.setItems(model.getSongs());
            model.loadPlaylists();
            lstPlaylist.setItems(model.getPlaylists());

            lstPlaylist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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

//        // Adds a listener to any slider changes
//        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                double volume = newValue.doubleValue();
//                bllManager.setVolume(volume); //Passes the new volume to the Business Layer
//            }
//        });
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
    private void btnCloseClicked(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }


}